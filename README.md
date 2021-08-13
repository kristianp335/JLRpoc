# JLRPoc
POC to create menu structure based on web content

Steps to follow:
Go to Control Panel -> System Settings -> Third Party -> Knowledge menu configuration and fill in the following information
1. Base Folder : Folder Id of the root folder for the menu
2. Base Url: Prefix of friendly URL for the web content article
3. Structure key : 1 - Structure ID found in the Structure Template
4. Localised folder name : Name of expando field attribute (if using expando fields, give VIEW permission to necessary roles)


To create the Display Page Template:
1. Go to Design -> Page Template -> Design Page Template -> Add -> Choose Web Content Type and select appropriate Web Content Structure.
2. Insert a 2- Grid Element and insert widget KnowledgeMenu (from Sample Category) on the left and insert Card fragment on the right.
3. Map Card attributes to the Web Content Structure fields.
4. Setup display page template for the article:
   Edit content article ->  Display Page Template -> Choose Specific Display Page Template and select the appropriate Display Page Template name.
  



