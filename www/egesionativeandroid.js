var exec = require('cordova/exec');

var PLUGIN_NAME = "Egesionativeandroid";

var Egesionativeandroid = function () { }; 

Egesionativeandroid.iniciaServicioackgroudAndroid = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "iniciaServicioackgroudAndroid", []);
};
Egesionativeandroid.detenServicioackgroudAndroid = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "detenServicioackgroudAndroid", []);
};
Egesionativeandroid.conectaPulseraAndroid = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "conectaPulseraAndroid", []);
};
Egesionativeandroid.desconectaPulseraAndroid = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "desconectaPulseraAndroid", []);
};
Egesionativeandroid.obtenValoresRegistrados = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "obtenValoresRegistrados", []);
};
Egesionativeandroid.enviaValoresRegistradosEgesioDB = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "enviaValoresRegistradosEgesioDB", []);
};
Egesionativeandroid.obtenLecturaHeartJSON = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "obtenLecturaHeartJSON", []);
};
Egesionativeandroid.obtenLecturaOxygeJSON = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "obtenLecturaOxygeJSON", []);
};
Egesionativeandroid.obtenLecturaPressureJSON = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "obtenLecturaPressureJSON", []);
};
Egesionativeandroid.obtenLecturaTemperatureJSON = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "obtenLecturaTemperatureJSON", []);
};

module.exports = Egesionativeandroid;