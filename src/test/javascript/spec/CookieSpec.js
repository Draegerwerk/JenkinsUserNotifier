describe("Cookie tests", function() {
	var name = 'uuid';
	var value = '1234567890';
	var date = new Date(9999,29,12).toUTCString();

	it("Should set the cookie", function() {
		setCookie(name, value, date);

		var v = '';

		var cookies = decodeURIComponent(document.cookie).split(";");
		for(var i = 0; i < cookies.length; i++) {
			var c = cookies[i];
			while (c.charAt(0) == ' ') {
				c = c.substring(1);
			}
			if (c.indexOf(name) == 0) {
				v = c.substring(name.length + 1, c.length);
			}
		}

		expect(v).toBe(value);
	});

	it("Should the cookie", function() {
		var v = getCookie(name);

		expect(v).toBe(value);
	});

	it("Cookie should exist", function() {
		expect(cookieExists(name)).toBe(true);
	});

	it("Cookie should exist", function() {
		// remove cookie from browser
		document.cookie = name + "=" + ";path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT"

		expect(cookieExists(name)).toBe(false);
	});
});
