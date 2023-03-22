/**
  * @Description : FICHIER DESCRIPTIF DES FONCTIONS DE VALIDATION DES DONNEES COTE CLIENT
  * @Auteur : Francis
  * @Version : 1.0
  * @Date : 02/01/2011
  */


/**
  *  Autoriser la saisie des chiffres dans une zone de texte
  */
function controleChiffres(event) {
	
	/*
	// Compatibilité IE / Firefox
	if(!event && window.event) {
		event=window.event;
	}
	// IE
	if( (event.keyCode < 48 || event.keyCode > 57 || event.keyCode!=46) && event.keyCode!=8 && event.keyCode!=127 ) {
		event.returnValue = false;
		event.cancelBubble = true;
	}
	// DOM
	if( (event.which < 48 || event.which > 57 || event.keyCode!=46) && event.keyCode!=8 && event.keyCode!=127) {
		event.preventDefault();
		event.stopPropagation();
	}
	*/
	
	var IE5 = false;
	if (!event) var event = window.event;
	if (event.keyCode) { IE5= true; code = event.keyCode;}
	else if (event.which) code = event.which ;
	//test du code  
	if ( (code < 48 || code > 57) && event.keyCode!=46 ) {
		if(IE5) event.returnValue = false;
		else event.preventDefault();
	}
	
}

/**
  *  Autoriser la saisie des chiffres numeros de telephone dans une zone de texte
  */
function controleNumerosPhone(event) {
	
	// Compatibilité IE / Firefox
	if(!event && window.event) {
		event=window.event;
	}
	
	// IE
	if( (event.keyCode < 48 || event.keyCode > 57) && event.keyCode!=8 && event.keyCode!=127 && event.keyCode != 43 && event.keyCode != 40 && event.keyCode != 41) {
		event.returnValue = false;
		event.cancelBubble = true;
	}
	// DOM
	if( (event.which < 48 || event.which > 57) && event.keyCode!=8 && event.keyCode!=127 && event.keyCode != 43 && event.keyCode != 40 && event.keyCode != 41) {
		event.preventDefault();
		event.stopPropagation();
	}
}


/**
 *  Autoriser la saisie des chiffres dans une zone de texte
 */
function controleTaille(event, id, taille) {
	// Compatibilité IE / Firefox
	if(!event && window.event) {
		event=window.event;
	}
	// IE
	if( document.getElementById(id).value.length == taille) {
		event.returnValue = false;
		event.cancelBubble = true;
	}
	// DOM
	if( document.getElementById(id).value.length == taille ) {
		event.preventDefault();
		event.stopPropagation();
	}
}

/**
 * Conversion le texte d'une zone de saisie en majuscule
 */
function metEnMajuscule(id) {
	document.getElementById(id).value = document.getElementById(id).value.toString().toUpperCase();
}

function startWaitInStyle() {
	document.getElementById('WaitDialog').style.display = 'block';
}
			
function stoptWaitInStyle() {
	document.getElementById('WaitDialog').style.display = 'none';
}