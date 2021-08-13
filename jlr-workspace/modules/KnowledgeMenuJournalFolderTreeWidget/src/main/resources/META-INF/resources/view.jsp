<%@ include file="./init.jsp" %>

<script> //BUILD A TREE OBJECT
	myArticleId=<%=request.getAttribute("articleId")%>;
	baseFolder=<%=request.getAttribute("baseFolder")%>;
	trees=<%=request.getAttribute("folders")%>
		.map(f=>({children:[],...f}))
		.reduce((a,f)=>({[f.id]:f,...a}), ({"0":{id:"0",parent:"0",name:"0",children:[]}}));;
	
	<%=request.getAttribute("articles")%>.forEach(a=>{
		if(!trees[a.folder].articles)trees[a.folder].articles=[];
		trees[a.folder].articles.push(a);
		if(myArticleId && myArticleId==a.id)trees[a.folder].expand=true;
	});
		
	Object.keys(trees).forEach(k=>{
		let f=trees[k];
		if(k=="0")return;
		if(f.expand)trees[f.parent].expand=true;
		if(!trees[f.parent].children)trees[f.parent].children=[];
		trees[f.parent].children.push(f);
	});
	
	tree=trees[baseFolder];
</script>

<ul id="contentTree"></ul>

<aui:script use="node">
	function addTogglerToCaret(caret){
  		caret.addEventListener("click", function() {
    			this.parentElement.querySelector(".nested").classList.toggle("active");
    			this.classList.toggle("caret-down");
  		});
	}
	function addChildToDomFolder(domE,ch){
		let li=document.createElement("li");
		if(ch.title)
			li.innerHTML='<a href="'+ch.friendlyUrl+'">'+ch.title+'</a>';
		else{
			let [spanClass,ulClass]=[ch.expand?"caret caret-down":"caret",ch.expand?"nested active":"nested"];
			li.innerHTML='<span class="'+spanClass+'">'+ch.name+'</span><ul class="'+ulClass+'"></ul>';
		}
	    domE.append(li);
	    return li;
	}
	function recursivePaintTree(domE,t){
		t.children.forEach(f=>{
			newE=Array.from(addChildToDomFolder(domE,f).getElementsByTagName("ul"))[0];
			recursivePaintTree(newE,f);	
		});
		if(t.articles)t.articles.forEach(a=>addChildToDomFolder(domE,a));
	}
	
	recursivePaintTree(A.one("#contentTree"),tree);//THIS ADDS THE WHOLE TREE STRUCTURE
	Array.from(document.getElementsByClassName("caret")).forEach(caret=>addTogglerToCaret(caret));//THIS ADDS THE TOGGLE FUNCTIONALITY
</aui:script>
