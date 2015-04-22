(function () {
	'use strict';

	$.extend(true, $, {
		'app': {
			'interface': window.b9js,
			'util': {
				'focus': function($el) {
					var el = $el.get(0);
					if (el) {
						el.focus();
						var html5types = ['password', 'email', 'url', 'tel', 'number', 'range', 'time'];
						if (html5types.indexOf(el.type) == -1 && el.setSelectionRange) {
							el.setSelectionRange(0, 0);
						} else if (el.createTextRange) {
							var range = el.createTextRange();
							range.collapse(true);
							range.moveEnd('character', 0);
							range.moveStart('character', 0);
							range.select();
						}
					}
				},
				splitArray: function (val) {
					var vals = $.trim(val);
					return (vals == '') ? [] : vals.split(',');
				},
				detectInputFeature: function (prop) {
					var bool;
					var inputElemType;
					var defaultView;
					var smile = ':)';
					var inputElem = document.createElement('input');
					inputElem.setAttribute('type', inputElemType = prop);
					bool = inputElem.type !== 'text';

					// We first check to see if the type we give it sticks..
					// If the type does, we feed it a textual value, which shouldn't be valid.
					// If the value doesn't stick, we know there's input sanitization which infers a custom UI
					if (bool) {

						inputElem.value = smile;
						inputElem.style.cssText = 'position:absolute;visibility:hidden;';

						if (/^range$/.test(inputElemType) && inputElem.style.WebkitAppearance !== undefined) {
							docElement.appendChild(inputElem);
							defaultView = document.defaultView;

							// Safari 2-4 allows the smiley as a value, despite making a slider
							bool = defaultView.getComputedStyle &&
							defaultView.getComputedStyle(inputElem, null).WebkitAppearance !== 'textfield' &&
								// Mobile android web browser has false positive, so must
								// check the height to see if the widget is actually there.
							(inputElem.offsetHeight !== 0);

							docElement.removeChild(inputElem);

						} else if (/^(search|tel)$/.test(inputElemType)) {
							// Spec doesn't define any special parsing or detectable UI
							//   behaviors so we pass these through as true

							// Interestingly, opera fails the earlier test, so it doesn't
							//  even make it here.

						} else if (/^(url|email|number)$/.test(inputElemType)) {
							// Real url and email support comes with prebaked validation.
							bool = inputElem.checkValidity && inputElem.checkValidity() === false;

						} else {
							// If the upgraded input compontent rejects the :) text, we got a winner
							bool = inputElem.value != smile;
						}
					}
					return !!bool;
				},
				numberFormat: function(val) {
					return val.toFixed(0).replace(/\d(?=(\d{3})+\.)/g, '$1,');
				}
			}
		}
	});

	$(function () {
		// android touch bug fix : (position:fixed; click bug)
		$('body').bind('touchstart', function() { return true; });
	});
})();