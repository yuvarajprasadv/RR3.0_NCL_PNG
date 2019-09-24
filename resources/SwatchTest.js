
var docRef = app.activeDocument;  

main();
function main()
{ 
var swatchList;
 for (var i = 0; i < app.activeDocument.swatches.length; i++)
 {
  	  var swatchObj = app.activeDocument.swatches[i];
 	if(app.activeDocument.swatches.length - 1 != i)
 		swatchList += swatchObj.name + '~';
 	else
 		swatchList += swatchObj.name;
 	
 }
 return swatchList;
}
  