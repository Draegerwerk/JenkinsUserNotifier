function hide_notifier(id) {
	var e = document.getElementById(id);

	// get the div that defines the height of the breadcrumbBar
	var bar = document.getElementById("breadcrumbBar");
	var heightdiv = null;
	var descendants = bar.getElementsByTagName('div');
	if ( descendants.length )
    	heightdiv = descendants[0];
    	var current_height = Number(heightdiv.style.height.slice(0, -2));
    	var remove_height = Number(e.offsetHeight);
    	var new_height = current_height - remove_height;
    	heightdiv.style.height = '' + new_height + "px";

	// Hide the Notification Bar
	e.style.display = 'none';
}