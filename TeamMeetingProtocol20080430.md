Willi:
  * implemented new attachment of intermediate events on activities
  * will start working on overlay

Weske suggested to implement an image export via URL
  * Hagen proposed to use a ".jpg" suffix behind the process URL to access an image of the process
  * First, the SVG representation of a process has also to be stored in the database

Björn:
  * started user tests by creating the process models from the BPT book.
  * Needs test server for load tests. Daniel has to create it.

Nico has to create a document, how to use Google Code for the project and add wiki content.

Hagen:
  * two students are evaluating HTML canvas technology for future enhancement of Oryx.

Sven:
  * started implementing BPMN1.1 stencil set
  * is searching for a Json editor

Nico has to review Sven's how to create a stencil set guide.

Ole:
  * server is running quite well now
  * saving a process works, but is not user friendly, e.g. you cannot set a name for a new process

Process is gone after a reload:
  * The problem is the 'new' URL for a newly created process. When reloading another new process is created. Ole has to solve it.

Ole:
  * PDF export is not working correctly due to some missing packages.
  * PNML export is dependant on PN-Engine.

Martin:
  * Created a document on how to update the server. Added to the Google Group.
  * OpenID is working on server-side, but it's not fully implemented on client-side.
  * Will create a document on how to configure the tomcat.
  * Will create a document on how to set up a development environment for Oryx on MacOSX.
  * Will clean up the DataManager.