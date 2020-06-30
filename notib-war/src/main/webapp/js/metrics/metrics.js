function drawLlegendaGraph(missatges) {
	console.log(missatges);
	var ctx_timer = document.getElementsByClassName('llegendaChart')[0];
	var frequecyChart = new Chart(ctx_timer, {
	    type: 'horizontalBar',
	    data: {
		    labels: ['4','2','5'],
	        datasets: [{
		            data: ['4','2','5'],  //Recupera les dades del json
		            backgroundColor: [
		                'rgba(114, 147, 203, 0.6)',
		                'rgba(132, 186, 91, 0.6)',
		                'rgba(225, 151, 76, 0.6)'
		            ],
		            borderWidth: 1
	        	}]
		},
	    options: { 
	    	maintainAspectRatio: false,
	    	aspectRatio: 1,
	    	tooltips: {
	            enabled: false
	       	},
			legend: {
	    		display: false
	    	},
	        title: {
	          	display: false
	        },
	        scales: {
	            yAxes: [{
	                ticks: {
	                	display: false,
	                }
	            }],
	            xAxes: [{
	            	ticks: {
	            		display: false,
	            		beginAtZero: true
                	}
            	}]
	        },
	        plugins: {
	            datalabels: {
	                // and if you need to format how the value is displayed...
	                formatter: function(value) {
	                	switch (value) {
	    				case '4':
	    					value = missatges[0];
	    					break;
	    				case '2':
	    					value = missatges[1];
	    					break;
	    				case '5':
	    					value = missatges[2];
	    					break;
	    				}
	                	return value;
	                }
	            }
	        }
	    }
	});
}

function drawTimersGraph(metricsData, missatges) {
	drawLlegendaGraph(missatges);
	var timersList = new Array();
	var maxPes = 0;
	var maxMax = 0;
	
	for (var timer in metricsData.timers) {
		var mitja = metricsData.timers[timer].mean;
		var maxim = metricsData.timers[timer].max;
		var num = metricsData.timers[timer].count;
		var pes = mitja * num; 
		timersList.push({name: timer, count: num, mean: mitja, max: maxim, weight: pes});
		if (pes > maxPes)
			maxPes = pes;
		if (maxim > maxMax)
			maxMax = maxim;
	}
			
	timersList.sort(function(a, b) {
		if (a.weight < b.weight) {
			return 1;
		}
		if (a.weight > b.weight) {
			return -1;
		}
		return 0;
	});
	
	for (var i in timersList) {
		var timer = timersList[i];
		
		var index = timer.name.lastIndexOf('.');
		var classTimer = timer.name.substring(0, index);
		var metricNameTimer = timer.name.substring(index + 1);
		//var titol = timer.substring(timerKeys[i].indexOf('helium'));
		//var pctPes = timer.weight * 100 / maxPes;
		//var pctMitja = timer.mean * 100 / maxMax;
		//var pctMaxim = (timer.max - timer.mean) * 100 / maxMax;
		var pes = Math.round(timer.weight * 100) / 100;
		var mitja = Math.round(timer.mean * 100) / 100;
		var maxim = Math.round(timer.max * 100) / 100;
			
		var nomSeccio = "timers-generics";

		var seccio = $("#" + nomSeccio);
					
		seccio.removeClass("ocult");
		seccio.prev().removeClass("ocult");
		seccio.append(
				"<div class='panel panel-default timers' data-id='" + timer.name + "' data-class='" + classTimer + "' data-nom='" + metricNameTimer + "' data-max-target='" + timer.count + "' data-max-timer='" + timer.max + "'>" +
				"	<div class='panel-body timer-body'>" +
				"		<div class='counterTitle'>" + timer.name + " (" + timer.count + " execucions)</div>" +
				//"		<div style='width: 100%; margin-left: 8px;'><div style='width:"+pctPes+"%; background-color:red; height: 1px;'></div></div>"+
				"		<canvas class='timerChart collapse' width='100' height='30'></canvas>"+
				"	</div>" +
				"	<div style='clear:both;'></div>"+
				"</div>"+
				"<div class='panel-body collapse' id='" + timer.name + "'>" +
				"	<div class='chart-container'><canvas class='frequecyChart collapse' width='500' height='200'></canvas></div>" +
				"	<div class='chart-container'><canvas class='durationChart collapse' width='500' height='200'></canvas></div>" +
				"	<div class='chart-container'><canvas class='percentileChart collapse' width='500' height='200'></canvas></div>" +
				"</div>");
		var ctx_timer = $("[data-id='" + timer.name + "']")[0].getElementsByClassName('timerChart')[0];
		var frequecyChart = new Chart(ctx_timer, {
		    type: 'horizontalBar',
		    data: {
				labels: ['min', 'mean', 'max'],
		        datasets: [{
			            data: [pes, mitja, maxim],  //Recupera les dades del json
			            backgroundColor: [
			            	'rgba(114, 147, 203, 0.4)',
			                'rgba(132, 186, 91, 0.4)',
			                'rgba(225, 151, 76, 0.4)'
			            ],
			            borderColor: [
			            	'rgba(114, 147, 203, 0.6)',
			                'rgba(132, 186, 91, 0.6)',
			                'rgba(225, 151, 76, 0.6)'
			            ],
			            borderWidth: 1
		        	}]
			},
		    options: { 
		    	maintainAspectRatio: false,
				legend: {
		    		display: false
		    	},
		        title: {
		          	display: false
		        },
		        scales: {
		            yAxes: [{
		                ticks: {
		                	display: false,
		                }
		            }],
		            xAxes: [{
		            	ticks: {
		            		display: false,
		            		beginAtZero: true
	                	}
	            	}]
		        }
		    }
		});
	}
}

function drawTimerDetailedInfo(divTimer, metricsData, missatges) {
	var ctx_frq = document.getElementById(divTimer).getElementsByClassName('frequecyChart')[0];
	var ctx_dur = document.getElementById(divTimer).getElementsByClassName('durationChart')[0];
	var ctx_per = document.getElementById(divTimer).getElementsByClassName('percentileChart')[0];
	
	//Informació frequència
	var frequecyData = {
			labels: ['1m', '5m', '15m'],
	        datasets: [{
		            data: getFrequency(metricsData), //Recupera les dades del json
		            backgroundColor: [
		                'rgba(0, 120, 0, 0.4)',
		                'rgba(0, 120, 20, 0.4)',
		                'rgba(0, 120, 40, 0.4)'
		            ],
		            borderColor: [
		            	'rgba(0, 120, 0, 1)',
		                'rgba(0, 120, 20, 1)',
		                'rgba(0, 120, 40, 1)'
		            ],
		            borderWidth: 1,
		            datalabels: {
	                 	display: false
		            }
	        	}]
	};
	//Opcions frequència (mitja, títol...)
	var frequecyOptions = {
			maintainAspectRatio: false,
		    legend: {
		    		display: false
		    	},
		    title: {
		          display: true,
		          text: missatges[3], //Frequència
		          position: 'bottom'
		    },
		    scales: {
				yAxes: [{
					ticks: {
						max: Math.max.apply(null, frequecyData.datasets[0].data) + ((Math.max.apply(null, frequecyData.datasets[0].data)/300) * 50), //Arregla bug chart.js
			        	beginAtZero: true
					}
				}]
		    },
			annotation: {
				//mitja
					borderColor: 'red',
					annotations: [{
				    	type: 'line',
						mode: 'horizontal',
				    	scaleID: 'y-axis-0',
				    	value: getFrequency(metricsData)[3], //Recupera la mitja de la frequència del json
				    	borderColor: 'rgba(255, 0, 0, 0.4)',
				    	borderWidth: 4,
				    	label: {
				    		backgroundColor: 'rgba(48, 142, 71, 0.1)',
				    		fontFamily: "sans-serif",
				    		fontColor: "black",
				    		enabled: true,
				    		content: missatges[4] + getFrequency(metricsData)[3] //Mitja: ****
						}
					}]
			},
	        hover: {
				"animationDuration": 0
	        },
			animation: {
				duration: 1,
				onComplete: function() {
		    		addValueToBar(this.chart, this.data);
		    	}
	    	}
	};
	//Informació duració
	var durationData = {
			labels: ['min', 'mean', 'max'],
	        datasets: [{
		            data: getDuracio(metricsData),  //Recupera les dades del json
		            backgroundColor: [
		                'rgba(0, 63, 140, 0.4)',
		                'rgba(0, 63, 160, 0.4)',
		                'rgba(0, 63, 180, 0.4)'
		            ],
		            borderColor: [
		            	'rgba(0, 63, 140, 1)',
		                'rgba(0, 63, 160, 1)',
		                'rgba(0, 63, 180, 1)'
		            ],
		            borderWidth: 1,
		            datalabels: {
	                 	display: false
		            }
	        	}]
	};
	//Opcions duració (títol...)
	var durationOptions = {
			maintainAspectRatio: false,
			legend: {
	    		display: false
	    	},
	        title: {
	          	display: true,
	          	text: missatges[5], //Duració
	          	position: 'bottom'
	        },
	        scales: {
	            yAxes: [{
	                ticks: {
	                	max: Math.max.apply(null, durationData.datasets[0].data) + ((Math.max.apply(null, durationData.datasets[0].data)/300) * 50), //Arregla bug chart.js
	                    beginAtZero: true
	                }
	            }]
	        },
            hover: {
                "animationDuration": 0
            },
		    animation: {
		    	duration: 1,
              	onComplete: function() {
              		addValueToBar(this.chart, this.data);
              	}
           }
	};
	//Informació percentils
	var percentileData = {
			labels: ['50%', '75%', '95%','98%', '99%', '99.9%'],
	        datasets: [{
		            label: 'Percentil',
		            data: getPercentil(metricsData),  //Recupera les dades del json
		            backgroundColor: [
		                'rgba(119, 35, 60, 0.4)',
		                'rgba(119, 35, 70, 0.4)',
		                'rgba(119, 35, 90, 0.4)',
		                'rgba(119, 35, 110, 0.4)',
		                'rgba(119, 35, 130, 0.4)',
		                'rgba(119, 35, 150, 0.4)'
		            ],
		            borderColor: [
		            	'rgba(119, 35, 60, )',
		                'rgba(119, 35, 70, 1)',
		                'rgba(119, 35, 90, 1)',
		                'rgba(119, 35, 110, 1)',
		                'rgba(119, 35, 130, 1)',
		                'rgba(119, 35, 150, 1)'
		            ],
		            borderWidth: 1,
		            datalabels: {
	                 	display: false
		            }
	        	}]
	};
	//Opcions duració (títol...)
	var percentileOptions = {
			maintainAspectRatio: false,
			legend: {
	    		display: false
	    	},
	        title: {
	          	display: true,
	          	text: missatges[6], //Percentils
	          	position: 'bottom'
	        },
	        scales: {
	            yAxes: [{
	                ticks: {
	                	max: Math.max.apply(null, percentileData.datasets[0].data) + ((Math.max.apply(null, percentileData.datasets[0].data)/300) * 50), //Arregla bug chart.js
	                    beginAtZero: true
	                }
	            }]
	        },
            hover: {
                "animationDuration": 0
            },
		    animation: {
		    	duration: 1,
              	onComplete: function() {
              		addValueToBar(this.chart, this.data);
              	}
           }
	};
	var frequecyChart = new Chart(ctx_frq, {
	    type: 'bar',
	    data: frequecyData,
	    options: frequecyOptions
	});
	var durationChart = new Chart(ctx_dur, {
	    type: 'bar',
	    data: durationData,
	    options: durationOptions
	});
	
	var percentileChart = new Chart(ctx_per, {
	    type: 'bar',
	    data: percentileData,
	    options: percentileOptions
	});
	//Refresca informació al fer click
	frequecyChart.render();
	durationChart.render();
	percentileChart.render();
}

function addValueToBar(chartInstance, data) {
    ctx = chartInstance.ctx;
    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, Chart.defaults.global.defaultFontStyle, Chart.defaults.global.defaultFontFamily);
    ctx.textAlign = 'center';
    ctx.textBaseline = 'bottom';
	//posa el número damunt cada barra
    data.datasets.forEach(function(dataset, i) {
    	var meta = chartInstance.controller.getDatasetMeta(i);
      	meta.data.forEach(function(bar, index) {
	    	if ((index != 3 && bar._model.label != undefined) || (index == 3 && bar._model.label == "98%")) { //mitja i no un percentil
	           	var data = dataset.data[index];
	           	ctx.fillText(data, bar._model.x, bar._model.y - 20);
	    	}
   		});
   	});
}

//Recupera la informació de la frequència a mostrar
function getFrequency(timerData) {
	var frequency = [];
	//El valor normalment és molt petit, millor dividir per 10000
	var m1_rate_rounded = Math.round(timerData.m1_rate * 10000) / 10000;
	var m5_rate_rounded = Math.round(timerData.m5_rate * 10000) / 10000;
	var m15_rate_rounded = Math.round(timerData.m15_rate * 10000) / 10000;
	var mean_rate_rounded = Math.round(timerData.mean_rate * 10000) / 10000;

	frequency.push(m1_rate_rounded);
	frequency.push(m5_rate_rounded);
	frequency.push(m15_rate_rounded);
	frequency.push(mean_rate_rounded);
	return frequency;
}

//Recupera la informació de la duració a mostrar
function getDuracio(timerData) {
	var duracio = [];
	var min_rounded = Math.round(timerData.min * 100) / 100;
	var mean_rounded = Math.round(timerData.mean * 100) / 100;
	var max_rounded = Math.round(timerData.max * 100) / 100;
	duracio.push(min_rounded);
	duracio.push(mean_rounded);
	duracio.push(max_rounded);
	return duracio;
}

//Recupera la informació dels percentils a mostrar
function getPercentil(timerData) {
	var percentils = [];
	var p50_rounded = Math.round(timerData.p50 * 100) / 100;
	var p75_rounded = Math.round(timerData.p75 * 100) / 100;
	var p95_rounded = Math.round(timerData.p95 * 100) / 100;
	var p98_rounded = Math.round(timerData.p98 * 100) / 100;
	var p99_rounded = Math.round(timerData.p99 * 100) / 100;
	var p999_rounded = Math.round(timerData.p999 * 100) / 100;
	percentils.push(p50_rounded);
	percentils.push(p75_rounded);
	percentils.push(p95_rounded);
	percentils.push(p98_rounded);
	percentils.push(p99_rounded);
	percentils.push(p999_rounded);
	return percentils;
}
