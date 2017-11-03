/*
	The MIT License

	Copyright (c) 2017, Draegerwerk AG & Co. KGaA , Yannik Petersen

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

// hides the NotificationBar and sets the cookie
function hideNotifier(uuid, expiration_date) {
	// hides the Notification Bar
	hideBar();

	// remember Hiding for current User as Cookie
	setCookie(uuid, uuid, expiration_date);
}

// hides the Notification Bar
function hideBar() {
	var e = document.getElementById('notifier_container');

	// get the div that defines the height of the breadcrumbBar
	var bar = document.getElementById("breadcrumbBar");
	var height_div = null;
	var descendants = bar.getElementsByTagName('div');
	if ( descendants.length ) {
    	height_div = descendants[0];
    	var current_height = Number(height_div.style.height.slice(0, -2));
    	var remove_height = Number(e.offsetHeight);
    	var new_height = current_height - remove_height;
    	height_div.style.height = new_height + "px";
    }

	// hide the Notification Bar
	e.style.display = 'none';
}

// sets a given cookie to a given value with the given expiration date (as string "MM/dd/yyyy")
function setCookie(name, value, expiration_date) {
	var date_parts = expiration_date.split('/');
	var expiration_date = new Date(date_parts[2],date_parts[0]-1,date_parts[1]);
	expiration_date.setDate(expiration_date.getDate() + 1);
	document.cookie = name + "=" + value + "; expires=" + expiration_date.toUTCString() + "; path=/";
}

// returns the value of the given cookie if it exists
function getCookie(c_name) {
    var name = c_name + "=";
    var decodedCookieList = decodeURIComponent(document.cookie);
    var cookies = decodedCookieList.split(';');
    for(var i = 0; i < cookies.length; i++) {
        var c = cookies[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

// checks if the given cookie exists or not
function cookieExists(uuid) {
	var cookie = getCookie(uuid);
	if (cookie == null || cookie == "") {
		return false;
	}
	return true;
}