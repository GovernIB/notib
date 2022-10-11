package com.example.statemachine.enums;

import lombok.Getter;

@Getter
public enum NomStateMachine {

    NOT("NOT"),
    COM_SIR("COM_SIR"),
    UPDATE_COM_SIR("UPDATE_COM_SIR"),
    UPDATE_NOT("UPDATE_NOT")
    ;

    private final String nom;

     NomStateMachine(String nom) {
        this.nom = nom;
    }

}
