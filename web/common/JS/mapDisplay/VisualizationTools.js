/**
 * Changes the color of links.
 */
function changeColor(btn,pos){
	switch(pos){
		case 0: // When the mouse is not over and is not being clicked
			btn.style.color = colorLink;
			break;
		case 1: // When the mouse is over
			btn.style.color = colorLinkOver;
			break;
		case 2: // When the button is being clicked
			btn.style.color = colorLinkClick;
			break;
		case 3: // When the button is disabled
			btn.style.color = colorLinkDisabled;
			break;

	}
}

