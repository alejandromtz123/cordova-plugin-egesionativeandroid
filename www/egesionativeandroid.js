var exec = require('cordova/exec');

var PLUGIN_NAME = "Egesionativeandroid";

var Egesionativeandroid = function () { }; 

Egesionativeandroid.iniciaServicioackgroudAndroid = function (arg0, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "iniciaServicioackgroudAndroid", [arg0]);
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
Egesionativeandroid.isPulseraConectadaAndroid = function (arr, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "isPulseraConectadaAndroid", arr);
};

module.exports = Egesionativeandroid;