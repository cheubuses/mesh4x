# Introduction #
Sahana is a web based Disaster Management application which is Free and Open source.
NGO’s , Government’s, humanitarian organization, can use sahana to track missing people during crisis situation. You can store/search information of volunteer, shelter and missing people. Three’s lot more about sahana  at http://www.sahana.lk/. This document describes how two instance of sahana  mysql tables can be synchronized through mesh4x.It also describes cloud synchronization between sahana tables and Mesh4x Cloud server(FeedSync Server).


Steps to sync sahana tables(Shelter Info) in 2 different Sahana installations (named as Local and Remote) using Mesh4x technologies.


# Method 1: mysql-to-mysql #

**Prerequisites**:
  * Install JDK (Version 1.5 or later) <br>
<ul><li>Install Mesh4x <b>EktooClient</b> (<a href='http://code.google.com/p/mesh4x/wiki/HowToUseEktooClient'>Ektoo Client</a>).</li></ul>


<b>Steps</b>:<br>
<br>
<ul><li>1. Allow remote mysql database access of Sahana instance from the machine where Ektoo Client will be executed.</li></ul>

<ul><li>2. Log in as 'admin' in local Sahana instance.</li></ul>

<ul><li>3. Add 3 Shelter Info (named as GSL, GKL, GKS) using <a href='Sahana.md'>Main (local)</a>/Shelter Registry>Add shelter.</li></ul>

<ul><li>4. Go to <a href='Sahana.md'>Main (local)</a>/Shelter Registry>View all shelters. This will show 3 Records (named as GSL, GKL, GKS).</li></ul>


<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_1.jpg' />


<ul><li>5. Log in as 'admin' in remote Sahana instance.</li></ul>

<ul><li>6. Go to <a href='Sahana.md'>Main (remote)</a>/Shelter Registry>View all shelters. This will show "No Records found".</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_2.jpg' />

<ul><li>7. Start Ektoo Client and configure source and target window for sync mysql tables (do select the tables mentioned earlier) and then press the sync button. Make sure you see the message "Synchronized Successfully" at the bottom left corner of the Ektoo client.</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_3.jpg' />

<ul><li>8. Go to <a href='Sahana.md'>Main (remote)</a>/Shelter Registry>View all shelters. This will show the 3 records added earlier in local Sahana instance.</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_4.jpg' />

<ul><li>9. Add 1 Shelter Info (named as GDL) in remote Sahana instance using <a href='Sahana.md'>Main (remote)</a>/Shelter Registry>Add shelter.</li></ul>

<ul><li>10. Go to <a href='Sahana.md'>Main (remote)</a>/Shelter Registry>View all shelters. This will show the 4 records (named as GSL, GKL, GKS and GDL).</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_5.jpg' />

<ul><li>10. Start and configure Ektoo Client as like earlier and sync those tables again. For this test it’s already configured so just press the sync button. Make sure you see the message "Synchronized Successfully" at the bottom left corner of the Ektoo client.</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_6.jpg' />

<ul><li>11. Go to <a href='Sahana.md'>Main (local)</a>/Shelter Registry>View all shelters. This will now show 4 Records (named as GSL, GKL, GKS and GDL) among which the 4th one (named as GDL) being added in remote Sahana instance .</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_7.jpg' />

<h1>Method 2: mysql-cloud-mysql</h1>


The same sync operation can be done via Mesh4x FeedSync Server .<br>
<br>
<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_8.jpg' />

Prerequisites:<br>
<ul><li>Install JDK (Version 1.5 or later)<br>
</li><li>Mesh4x Ektoo Client (It’s an executable jar file)<br>
</li><li>Mesh4x FeedSync Server installed, running and accessible via http from the machine where Ektoo Client will be executed.</li></ul>

Following are the high level steps to accomplish that:<br>
<br>
<ul><li>1.	Login to local Sahana instance add Shelter info.</li></ul>

<ul><li>2.	Sync mysql tables related to Shelter Registry in local Sahana instance to FeedSync Server using Ektoo client.</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_9.jpg' />

<ul><li>3.	Check the feed named ‘sahana’ in FeedSync server .</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_10.jpg' />

<ul><li>4.	Sync mysql tables related to Shelter Registry in remote Sahana instance to FeedSync Server using Ektoo client .</li></ul>

<img src='http://mesh4x.googlecode.com/svn/wiki/files/sync_sahana_11.jpg' />

<ul><li>5.	Login to remote Sahana instance and view all Shelters. The newly added Shelters in local Sahana instance will now be available in remote Sahana instance.