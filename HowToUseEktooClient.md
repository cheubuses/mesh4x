<table width='100%' border='0'>
<blockquote><tr>
<blockquote><td width='40%' valign='top'>
<blockquote>
</blockquote></td>
<td width='60%' align='center' valign='top'>
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_1.2.jpg' />
<br />
<b>Get Ektoo Client Now</b> <a href='http://downloads.instedd.org/mesh4x/ektoo_2_0_0.zip'><img src='http://mesh4x.googlecode.com/svn/wiki/files/download_2.png' /></a></blockquote></blockquote>

</td>
<blockquote></tr></blockquote>

</table>




<table width='100%' valign='top'>

<blockquote><tr>
<blockquote><td width='50%' valign='top'></blockquote></blockquote>

<h1>Introduction</h1>
Ektoo is a desktop client that helps user to synchronize data between various data repositories using mesh4x.<br>
<br>
This step-by-step guide shows how a user can use Ektoo client to synchronize data between various data repositories.<br>
<br>
<h1>Overview</h1>

Currently mesh4x supports following data repositories:<br>
<ol><li>KML<br>
</li><li>Rss 2.0<br>
</li><li>Atom 1.0<br>
</li><li>Microsoft Excel<br>
</li><li>Microsoft Access<br>
</li><li>Google Spreadsheet<br>
</li><li>MySQL and other databases<br>
</li><li>Feed or Amazon S3 (cloud storage)<br>
</li><li>Folder</li></ol>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_cloud_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_kml_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_gs_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_excel_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_access_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_mysql_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_rss_plain.png' />
<img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_folder_plain.png' />


<blockquote></td>
<td></td>
</blockquote><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h1>Prerequisites</h1>
You will need:<br>
</blockquote></blockquote><ul><li>JRE(Java Runtime Environment) 1.5 or later version installed in your system.<br>
</li><li>Ektoo client installed in your system<br>
<blockquote></td>
<td></td>
</blockquote><blockquote></tr></blockquote></li></ul>


<blockquote><tr>
<blockquote><td valign='top'>
<h1>Download and Installation</h1>
</blockquote></blockquote><ol><li>Download the latest version Ektoo client release (<a href='http://downloads.instedd.org/mesh4x/ektoo_2_0_0.zip'>Ektoo Client</a>).<br>
</li><li>Extract the zip distribution to a suitable location. The distribution contains 4 folders and a .jar file as the following:<br>
<ul><li>data : Contains sample/default data files<br>
</li><li>database : Contains sample db schema<br>
</li><li>logs : Contains application log<br>
</li><li>properties : Contains application runtime properties and locale file<br>
</li><li>ektooClient.jar : Ektoo client as an executable jar archive<br>
</li></ul></li><li>Double click the ektooClient.jar to run the program. You can also run the jar from command line as the following<br>
<pre><code>java -jar [folderLocation/]ektooClient.jar<br>
</code></pre>
<blockquote></td>
<td></td>
</blockquote></li></ol><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h1>Steps to Synchronize</h1>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select data source type from drop down list of source pane.<br>
<ul><li>Enter details information to define source data source.<br>
</li></ul></li><li>Select target data source type from drop down list of target pane.<br>
<ul><li>Enter details information to define target data source.<br>
</li></ul></li><li>Click on "<b>Synchronize Now</b>" button to start synchronization process.<br>
<blockquote></td>
<td></td>
</blockquote><blockquote></tr>
<tr>
<blockquote><td valign='top'>
<h1>How-To-Define Data Source</h1>
Defining various data source as source or target is a very simple process. Following sections describe how-to-define a data source of your desire data source type.<br>
</td>
<td></td>
</blockquote></tr></blockquote></li></ol>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>KML</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "KML" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Browse KML files and select one that you want to synchronize.</li></ol>

<blockquote></td>
<blockquote><td><img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_10.gif' /></td>
</blockquote></blockquote><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>Rss 2.0</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "Rss 2.0" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Browse rss XML files and select one that you want to synchronize.<br>
<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_7.gif' /></td>
</blockquote><blockquote></tr></blockquote></li></ol>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>Atom 1.0</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "Atom 1.0" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Browse atom xml files and select one that you want to synchronize.</li></ol>

<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_8.gif' /></td>
</blockquote><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>Microsoft Excel</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "MS Excel" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Browse Excel files and select one that you want to synchronize.<br>
<ul><li>Available list of worksheet will be displayed in "<b>Worksheet</b>" drop down list.<br>
</li></ul></li><li>Select worksheet from "<b>Worksheet</b>" drop down list.<br>
<ul><li>Available list of column will be displayed in "Unique Column" drop down list.<br>
</li></ul></li><li>Select unique column from "<b>Unique Column</b>" drop down list.</li></ol>

<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/excel.jpg' /></td>
</blockquote><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>Google Spreadsheet</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "Google Spreadsheet" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Enter user name in "<b>User</b>" text box<br>
</li><li>Enter password in "<b>Password</b>" text box<br>
</li><li>Click on connection icon<br>
<ul><li>After successful connection, all spreadsheet will be displayed in "<b>Spreadsheet</b>" drop down list<br>
</li></ul></li><li>Select a spreadsheet from the "<b>Spreadsheet</b>" drop down list<br>
<ul><li>Available worksheets will be displayed in "<b>Worksheet</b>" drop down list<br>
</li></ul></li><li>Select a worksheet from "<b>Worksheet</b>" drop down list.<br>
<ul><li>Available list of column will be displayed in "Unique Column" drop down list.<br>
</li></ul></li><li>Select unique column from "<b>Unique Column</b>" drop down list.</li></ol>

<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/google-spreadsheet.jpg' /></td>
</blockquote><blockquote></tr></blockquote>


<blockquote><tr>
<blockquote><td valign='top'>
<h2>Microsoft Access</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "MS Access" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Browse Access database files and select one that you want to synchronize.<br>
<ul><li>Available list of table will be displayed in "<b>Table</b>" drop down list.<br>
</li></ul></li><li>Select table from "<b>Table</b>" drop down list.</li></ol>

<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/access.jpg' /></td>
</blockquote><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>MySQL</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "MySQL" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Enter user name in "<b>User</b>" text box<br>
</li><li>Enter password in "<b>Password</b>" text box<br>
</li><li>Enter host name and port no in "<b>Host</b>" first and second text box.<br>
</li><li>Enter database in "<b>Database</b>" text box<br>
</li><li>Click on connection icon<br>
<ul><li>After successful connection, available tables will be displayed in "<b>Table</b>" drop down list<br>
</li></ul></li><li>Select table from "<b>Table</b>" drop down list.<br>
<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/mysql.jpg' /></td>
</blockquote><blockquote></tr></blockquote></li></ol>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>Cloud</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Enter the Mesh name in "<b>Mesh Name</b>" text box.<br>
</li><li>Enter the Feed/Data set name in "<b>Data Set</b>" text box.<br>
</li><li>Enter the URI of sync server in "<b>Sync Server Uri</b>" text box.</li></ol>

<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/cloud.jpg' /></td>
</blockquote><blockquote></tr></blockquote>

<blockquote><tr>
<blockquote><td valign='top'>
<h2>Folder</h2>
The process consists of:<br>
</blockquote></blockquote><ol><li>Select "Folder" data type from "<b>Data Source Type</b>" drop down list.<br>
</li><li>Browse folders and select one that you want to synchronize.<br>
<blockquote></td>
<td><img src='http://mesh4x.googlecode.com/svn/wiki/files/ektoo_9.gif' /></td>
</blockquote><blockquote></tr></blockquote></li></ol>

</table>