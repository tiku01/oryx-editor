/* This file is adapted from Microsofts ClientKit and licensed as follows:
  
This license governs use of the accompanying software. If you use the
software, you accept this license. If you do not accept the license,
do not use the software.

1. Definitions
The terms reproduce, reproduction, derivative works, and 
distribution have the same meaning here as under U.S. copyright law.

A contribution is the original software, or any additions or changes
to the software.

A contributor is any person that distributes its contribution under
this license.

Licensed patents are a contributors patent claims that read 
directly on its contribution.

 2. Grant of Rights
(A) Copyright Grant- Subject to the terms of this license, including 
the license conditions and limitations in section 3, each contributor 
grants you a non-exclusive, worldwide, royalty-free copyright license 
to reproduce its contribution, prepare derivative works of its 
contribution, and distribute its contribution or any derivative works 
that you create.

(B) Patent Grant- Subject to the terms of this license, including the 
license conditions and limitations in section 3, each contributor 
grants you a non-exclusive, worldwide, royalty-free license under its 
licensed patents to make, have made, use, sell, offer for sale, 
import, and/or otherwise dispose of its contribution in the software 
or derivative works of the contribution in the software.

3. Conditions and Limitations
(A) No Trademark License- This license does not grant you rights to 
use any contributors name, logo, or trademarks.

(B) If you bring a patent claim against any contributor over patents
that you claim are infringed by the software, your patent license 
from such contributor to the software ends automatically.

(C) If you distribute any portion of the software, you must retain 
all copyright, patent, trademark, and attribution notices that are 
present in the software.

(D) If you distribute any portion of the software in source code 
form, you may do so only under this license by including a complete
copy of this license with your distribution. If you distribute any
portion of the software in compiled or object code form, you may 
only do so under a license that complies with this license.

(E) The software is licensed as-is. You bear the risk of using it.
The contributors give no express warranties, guarantees or 
conditions. You may have additional consumer rights under your local
laws which this license cannot change. To the extent permitted under
your local laws, the contributors exclude the implied warranties of 
merchantability, fitness for a particular purpose and non-infringement.

*/

function informationCardsSupported()  { 
    /// <summary>
    /// Determines if Information Cards are supported by the browser.
    /// </summary>
    /// <returns>true if the browser supports Information Cards.</returns>
    
    var IEVer = -1; 
    if (navigator.appName == 'Microsoft Internet Explorer') 
	if (new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})")
	    .exec(navigator.userAgent) != null) 
	    IEVer = parseFloat( RegExp.$1 ); 
    
    // Look for IE 7+. 
    if( IEVer >= 7 ) { 
	var embed = document.createElement("object"); 
	embed.setAttribute("type", "application/x-informationcard"); 
	
	return (""+embed.issuerPolicy != "undefined" && embed.isInstalled);
    }     
    // not IE (any version)
    if( IEVer < 0 && navigator.mimeTypes && navigator.mimeTypes.length)  { 
	// check to see if there is a mimeType handler. 
	x = navigator.mimeTypes['application/x-informationcard']; 
	if (x && x.enabledPlugin) 
	    return true;
	
	// check for the IdentitySelector event handler is there. 
	var event = document.createEvent("Events"); 
	event.initEvent("IdentitySelectorAvailable", true, true); 
	top.dispatchEvent(event); 
	
	if( top.IdentitySelectorAvailable == true) 
	    return true; 
    } 
    return false; 
}