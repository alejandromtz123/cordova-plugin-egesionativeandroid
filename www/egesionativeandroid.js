var exec = require('cordova/exec');

module.exports = {

	playVideo: function(callback_success, callback_error, options) {

        exec(callback_success, callback_error, "egesionativeandroid", "test", options);

	}
}