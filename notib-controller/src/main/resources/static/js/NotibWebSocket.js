class NotibWebSocket {

    stompClient = null;

    constructor() {
    }

    frameHandler(frame) {

        console.log('Connecting: ' + frame);
        this.stompClient.subscribe('/user/notibws/missatge', message => this.processarResposta(message.body));
    }

    onSocketClose = () => {

        if (this.stompClient !== null) {
            this.stompClient.deactivate();
        }
        console.log("Socket was closed. Setting connected to false!")
    };

    connectSockJs(){

        this.stompClient = new window.StompJs.Client({webSocketFactory: () => new window.SockJS("/notibback/websocket")});
        this.stompClient.onConnect = frame => this.frameHandler(frame);
        this.stompClient.onWebsocketClose = () => this.onSocketClose();
        this.stompClient.activate();
    };

    disconnect() {

        if (stompClient !== null) {
            stompClient.deactivate();
        }
        console.log("Disconnected");
    };

    processarResposta(resposta) {

        let json = JSON.parse(resposta);
        if (json.msg) {
            this.mostrarMissatge(json);
        }
        if (json.updateInfo) {
            this.updateNotInfo(json.info);
        }
    }

    mostrarMissatge(msg) {

        let classe = msg.ok ? "alert-success" : "alert-danger";
        let content = '<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true">' +
            '<span class="fa fa-times"></span></button>' + msg.msg + '</div>';

        let div = document.createElement("div");
        div.className = "alert " + classe;
        $(div).append(content);
        const frames = window.frames;
        if (frames.length > 0) {
            let cm = frames[frames.length-1].document.getElementById("contingut-missatges");
            $(cm).append(div);
        } else {
            $("#contingut-missatges").empty();
            $("#contingut-missatges").append(div);
        }
        window.setTimeout(() => div ? div.remove() : "", 4000);
    }

    updateNotInfo(info) {
        frames[frames.length-1].window.location.reload();
    }
}