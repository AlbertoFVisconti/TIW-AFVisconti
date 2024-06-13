{
	let alertContainer = document.getElementById("id_alert");
	let pageOrchestrator = new PageOrchestrator();
	let tree,folderDetails,docDetails,folderCreation,docCreation,ddManager;
	window.addEventListener("load", () => {
		pageOrchestrator.start();
		pageOrchestrator.refresh();
	  }, false);
	  
	  //this manages all the dd funcionality(move doc, move folder, remove doc, remove folder)
	  function DragDropManager(){
		  
		  //creates the trash element and add listener that delete the doc/folder dragged onto it 
		  const trash=document.querySelector('.trash');
		  var doc,fold;
		  trash.addEventListener('dragover',(e)=>{
				  e.preventDefault();
			  });
		  trash.addEventListener('drop',(e)=>{
				e.preventDefault();
				if(window.confirm("confirm the removal of the element")){
					if(doc){
						makeCall("POST", 'RemoveDoc?docid='+doc, null,
				          function(req) {
				            if (req.readyState == 4) {
				              var message = req.responseText;
				              if (req.status == 200) {
				               	tree.show(function() {  });
				              } else if (req.status == 403) {
			                  	window.location.href = req.getResponseHeader("Location");
			                  	window.sessionStorage.removeItem('username');
			                  }
			                  else {
				                alertContainer.textContent = message;
				                
				              }
				            }
				          }
				        );	
						}
					else if(fold){
						makeCall("POST", 'RemoveFolder?folderId='+fold, null,
				          function(req) {
				            if (req.readyState == 4) {
				              var message = req.responseText;
				              if (req.status == 200) {
				                tree.show(function() {  });
				              } else if (req.status == 403) {
			                  	window.location.href = req.getResponseHeader("Location");
			                  	window.sessionStorage.removeItem('username');
			                  }
			                  else {
				                alertContainer.textContent = message;
				                
				              }
				            }
				          }
				        );
					}
					
				}
				else{
					folderDetails.update();
					docDetails.update();
					folderCreation.reset();
					docCreation.reset();
				}
				

			 });
		  this.update=function(){
			  console.log(trash);
		  const draggables= document.querySelectorAll('.draggable');
		  const containers=document.querySelectorAll('.container');
		  
		  //add the logic to style to move the documents and folders 
		  draggables.forEach(function(draggable){
			  draggable.addEventListener('dragstart', ()=>{
					doc=draggable.getAttribute('docid');
					fold=draggable.getAttribute('folderid');
				  draggable.classList.add("dragging");
				  });
				draggable.addEventListener('dragend',()=>{
					draggable.classList.remove('dragging');
				});
			  
		  });
		  //add the logic to the folder as containers
		  containers.forEach(function(container){
			  container.addEventListener('dragover',(e)=>{
				  e.preventDefault();
			  });
			  container.addEventListener('drop',(e)=>{
				e.preventDefault();
				if(doc){
					let params={
						folderId:container.getAttribute('folderid'),
						docid:doc	
					}
					const queryString = new URLSearchParams(params).toString();
					makeCall("POST", 'MoveDoc?'+queryString, null,
			          function(req) {
			            if (req.readyState == 4) {
			              var message = req.responseText;
			              if (req.status == 200) {
			                tree.show(function() {  });
			              } 
		                  else {
			                alertContainer.textContent = message;
			                
			              }
			            }
			          }
			        );	
					}
	
			 });
		  });
		  
			 };		  
		  
	  }
	  
	  function CreateDoc(dWizard,alert){
		  this.dWizard=dWizard;
		  this.alert= alert;
		  this.folderid=this.dWizard.querySelector('input[name="folderid"]');
		  this.dWizard.style.visibility= "hidden";
		  // max date the user can choose, in this case now and in the past
	      var now = new Date(),
	      formattedDate = now.toISOString().substring(0, 10);
		  dWizard.querySelector('input[type="date"]').setAttribute("max", formattedDate);
		  
		  //make the doc input folder visbile so that the user can pass the values 
		  	this.show=function(folderId){
		  	this.dWizard.style.visibility= "visible";
			this.folderid.value=folderId;
			this.dWizard.querySelector("input[type='button']").addEventListener('click', (event) => {
				makeCall("POST", 'CreateDoc', event.target.closest("form"),
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                tree.show(function() {  });
	                docCreation.reset();
	              } else if (req.status == 403) {
                  	window.location.href = req.getResponseHeader("Location");
                  	window.sessionStorage.removeItem('username');
                  }
                  else {
	                alertContainer.textContent = message;
	                
	              }
	            }
	          }
	        );	
				
			});
			}
		  
		  
		  //hides the input form once it is done 
		  this.reset=function(){
			  this.dWizard.style.visibility= "hidden";
			  this.folderid.value="";
		  };
		  
		  
	  }
	  
	  //resuses the logic of creating a folder but the parentid will be 1
	  function CreateMainF(){
		  mainf=document.getElementById("id_mainfolderform");
		  mainf.querySelector("input[type='button']").addEventListener('click', (event) => {
	                folderCreation.show(1);
			});
	  }
	  
	  
	  function CreateFolder(fWizard, alert){
		  this.fWizard=fWizard;
		  this.alert= alert;
		  this.fWizard.style.visibility= "hidden";
		  this.folderid=this.fWizard.querySelector('input[name="folderid"]');
		  // max date the user can choose, in this case now and in the past
	      var now = new Date(),
	      formattedDate = now.toISOString().substring(0, 10);
	      fWizard.querySelector('input[type="date"]').setAttribute("max", formattedDate);
		  
		  //make the input form for the creation of a fodler visible so that the user can insert values 
		  this.show=function(folderId){
		  	this.fWizard.style.visibility= "visible";
			this.folderid.value=folderId;
			this.fWizard.querySelector("input[type='button']").addEventListener('click', (event) => {
				makeCall("POST", 'CreateFolder', event.target.closest("form"),
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                tree.show(function() {  });
	                folderCreation.reset();
	              } else if (req.status == 403) {
                  	window.location.href = req.getResponseHeader("Location");
                  	window.sessionStorage.removeItem('username');
                  }
                  else {
	                alertContainer.textContent = message;
	                
	              }
	            }
	          }
	        );	
				
			});

			  
			  
			  
		  }
		  //hides the input folder
		  this.reset=function(){
			  this.fWizard.style.visibility= "hidden";
			  this.folderid.value="";
		  };

		  
	  }
	  
	  function DocDetails(item){
		  this.alert=item['alert'];
		  this.nome=item['nome'];
		  this.data=item['data'];
		  this.psommario=item['psommario'];
		  this.sommario=item['sommario'];
		  this.ptipo=item['ptipo'];
		  this.tipo=item['tipo'];
		  this.detailcontainer=item['container'];
		  this.detailcontainer.style.visibility= "hidden";
		  
		  //gets the details of the document you want to show and makes the container visible 
		  this.show = function(docId) {
	      var self = this;
	      makeCall("GET", "GetDocDetailsData?docId=" + docId, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var doc = JSON.parse(req.responseText);
	              self.update(doc); 
	              self.detailcontainer.style.visibility = "visible";
	              }
	            } else if (req.status == 403) {
	              window.location.href = req.getResponseHeader("Location");
	              window.sessionStorage.removeItem('user');
	              }
	              else {
	              alertContainer.textContent = message;
	
	            }
	          }
	      );
	    };
	    
	    //insert into the container the value it read from the db 
	    this.update=function(d){
			this.nome.textContent=d.nome;
			this.data.textContent=d.date;
			this.psommario.style.visibility = "visible";
			this.sommario.textContent=d.sommario;
			this.ptipo.style.visibility = "visible";
			this.tipo.textContent=d.tipo;
		};
	  }
	  
	  function FolderDetails(item){
		  this.alert=item['alert'];
		  this.nome=item['nome'];
		  this.data=item['data'];
		  this.psommario=item['psommario'];
		  this.sommario=item['sommario'];
		  this.ptipo=item['ptipo'];
		  this.tipo=item['tipo'];
		  this.detailcontainer=item['container'];
		  this.detailcontainer.style.visibility= "hidden";
		  this.show = function(folderid) {
	      var self = this;
	      //gets the folder details from the db and makes the container visibile 
	      makeCall("GET", "GetFolderDetailsData?folderId=" + folderid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var folder = JSON.parse(req.responseText);
	              self.update(folder); 
	              self.detailcontainer.style.visibility = "visible";
	              }
	            } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('user');
                  }
                  else {
	              alertContainer.textContent = message;

	            }
	          }
	      );
	    };
	    
	    //puts the values inside the container 
	    this.update=function(f){
			this.nome.textContent=f.nome;
			this.data.textContent=f.date;
			this.psommario.style.visibility= "hidden";
			this.sommario.textContent='';
			this.ptipo.style.visibility= "hidden";
			this.tipo.textContent='';
		};
	    
	    
	    
	    
	  }
	  
	  function Tree(_alert, _listcontainer, _listcontainerbody){
		this.alert = _alert;
	    this.listcontainer = _listcontainer;
	    this.listcontainerbody = _listcontainerbody;
		  
		//used to create the folder+doc tree or dispaly an error 
		function createTree(folder, parent) {
			//creates the button to make a first lvl folder 
			var addFolder = document.createElement("button");
			        addFolder.textContent = "aggiungi cartella";
			        addFolder.addEventListener("click", (e) => {
			            e.stopPropagation(); // Prevent the click from triggering the anchor's click event
			            folderCreation.show(1);
			        });
		    var ul = document.createElement("ul");
		    
		    // Append documents
		if (folder.documents && folder.documents.length > 0) {
		    folder.documents.forEach(function(doc) {
		        var col = document.createElement("li");
		
		        // Create anchor element for document
		        var anchor = document.createElement("a");
		        var linkText = document.createTextNode(doc.nome);
		        anchor.appendChild(linkText);
		        anchor.setAttribute('docid', doc.docId);
		        anchor.setAttribute('draggable', true);
		        anchor.classList.add("draggable");
		        anchor.addEventListener("click", (e) => {
		            docDetails.show(e.target.getAttribute("docid"));
		        }, false);
		        anchor.href = "#";
		        col.appendChild(anchor);
		        ul.appendChild(col);
		    });
		}
		    
		     // Append subfolders
		    if (folder.subfolder && folder.subfolder.length > 0) {
		        folder.subfolder.forEach(function(subfolder) {
		            var col = document.createElement("li");
		            var anchor = document.createElement("a");
		            var linkText = document.createTextNode(subfolder.nome);
		            anchor.appendChild(linkText);
		            anchor.setAttribute('folderid', subfolder.folderId);
		            anchor.setAttribute('draggable', true);
		        	anchor.classList.add("draggable");
		            anchor.classList.add("container");
		            anchor.addEventListener("click", (e) => {
		               folderDetails.show(e.target.getAttribute("folderid"));
		            }, false);
		            anchor.href = "#";
		             // Create aggiungi sottocartella button
			        var addSubFolder = document.createElement("button");
			        addSubFolder.textContent = "aggiungi sottocartella";
			        addSubFolder.addEventListener("click", (e) => {
			            e.stopPropagation(); // Prevent the click from triggering the anchor's click event
			            folderCreation.show(subfolder.folderId);
			            docCreation.reset();
			        });
			
			        // Create aggiungi doc button
			        var addDoc = document.createElement("button");
			        addDoc.textContent = "aggiungi documento";
			        addDoc.addEventListener("click", (e) => {
			            e.stopPropagation(); // Prevent the click from triggering the anchor's click event
			            docCreation.show(subfolder.folderId);
			            folderCreation.reset();
			        });
		            col.appendChild(anchor);
		            col.appendChild(addSubFolder);
		            col.appendChild(addDoc);
		            ul.appendChild(col);	
		            createTree(subfolder, ul);
					});
				}

			parent.appendChild(ul); 
			}
		 
		 
		 //makes the previous tree disappear and creates a new one 
		this.update=function(rootFolder){
			tree.listcontainerbody.remove();
			tree.listcontainerbody=null;
			tree.listcontainerbody=document.createElement('tbody');
			tree.listcontainer.appendChild(tree.listcontainerbody);
			if (rootFolder) {
		        createTree(rootFolder, tree.listcontainerbody);
			}	
			ddManager.update();
		 } 
		  
		  //start of the chain, gets the folder and doc tree form the server 
		 this.show = function(next) {
	      var self = this;
	      makeCall("GET", "FolderData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var rootFolder = JSON.parse(req.responseText);
	              if (rootFolder.subfolder.length == 0) {
	                alertContainer.textContent = "No folders or files yet!";
	                return;
	              }
	              self.update(rootFolder); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('user');
                  }
                  else {
	            alertContainer.textContent = message;
	          }}
	        }
	      );
	  	}
	  }

		

	    
function PageOrchestrator(){
	
		  const info={
				//wrapping the param in an object
				alert:alertContainer,
				nome:document.getElementById("id_nome"),
				data:document.getElementById("id_data"),
				psommario:document.getElementById("id_psommario"),
				sommario:document.getElementById("id_sommario"),
				ptipo:document.getElementById("id_ptipo"),
				tipo:document.getElementById("id_tipo"),
				container:document.getElementById("id_detailcontainer")
			};
		  
		  this.start=function(){
			ddManager= new DragDropManager();
			tree = new Tree(alertContainer,document.getElementById("id_tree"),document.getElementById("id_treebody"));
			folderDetails= new FolderDetails(info);
			docDetails= new DocDetails(info);
			
			folderCreation= new CreateFolder(document.getElementById("id_folderform"),alertContainer);
			docCreation= new CreateDoc(document.getElementById("id_docform"),alertContainer);
			mainf=new CreateMainF();
			
			};
		  	
		  
		  this.refresh=function(){
			  alertContainer.textContent = "";  
			  tree.show(function() {  });
			  docCreation.reset();
			  folderCreation.reset();
		  }
	    };
}