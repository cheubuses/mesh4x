The goal for the project is to provide a portfolio of libraries, tools and applications that simplify using standards-based data meshes from multiple platforms and languages.

JoinTheMesh4xCommunity!

### Introduction ###
A data mesh allows information to be synchronized in a peer-to-peer way, allowing offline work, and synchronizing with whoever is available, not just a central database or a service on the internet. This makes it a perfect fit for situation where there is little/no connectivity or where the synchronization has to happen between different applications and services.

For example, one might create a data mesh that associates and synchronizes structured data from a Microsoft Access Database with a mobile phone running a Java XForms application and with an online Google spreadsheet.

Changes made on any one endpoint eventually sync to all of the other endpoints in the mesh. This architecture offers unique benefits for cross-organizational information sharing, particularly in austere communications environments, because it
  1. Is redundant, storing copies of data at each endpoint,
  1. Supports offline access to local data,
  1. Allows participating organizations to share common data while using the applications and devices best suited to their individual needs, and
  1. Provides a socially and politically "neutral" multi-master context in which no one organization owns or controls a single copy of a data set.


## Standards and Technologies ##
Mesh4x projects use many open standards to be interoperable.

The most critical standard is FeedSync:
FeedSync is an open protocol that describes data formats and algorithms used to version information in a mesh. It was created by [Ray Ozzie](http://en.wikipedia.org/wiki/Ray_Ozzie) and others in the Microsoft Concept Development Team, licensed under Creative Commons.

Non-sync technologies used in mesh4x include:

[RDF](http://en.wikipedia.org/wiki/Resource_Description_Framework) as the default data representation, and the server exposes XForms as one of the formats in which to expose data and schemas so UIs can render forms-based interfaces.

Some adapters also implement standards specific to their domains - for example, the map synchronization component uses the [KML](http://en.wikipedia.org/wiki/Keyhole_Markup_Language) standard to represent hierarchies of points, polygons, etc.


### Adapters ###
A data mesh allows two-way synchronization of information in a symmetric way, guaranteeing certain versioning behaviors regardless of the path data has taken. In this symmetrical exchange, you have an 'adapter' on each end. You could have an adapter to files, to a relational database, to HTTP exchange, etc.

See HowToBuildAnAdapter

See the list of [Adapters](Adapters.md) with brief descriptions.

[List Of Existing Adapters in the Task List](http://code.google.com/p/mesh4x/issues/list?can=2&q=component:Adapters&colspec=ID%20Type%20Status%20Priority%20Milestone%20Owner%20Summary%20Mesh%20Component%20Stars)

Want a new adapter? Propose it as an issue! Make sure it doesnt exist checking the list above and then create an issue.

## News: Download and trying it out: Use Fuse! ##

  * [See the tutorial to generate kml files with the ektoo client and the sync server (cloud)](KmlGeneration.md)
  * [See the JavaRosa tutorial](JavaRosaDemo.md). End to end scenario to synchronize a MsAccess file between the ektoo client and the cloud and between the cloud and the JavaRosa mobile client.
  * Go to [Mesh4xDownloads](Mesh4xDownloads.md) to see all available downloads (tools, examples and products).
  * [US CDC Recommends Mesh4x to Synchronize Data using Epi Info™](http://taha.instedd.org/search/label/Mesh)
  * We have just started working on a tool called 'Fuse' that you can use to create a mesh and connect your data to it. We call the first release of Fuse 'Ektoo' which means 'Small' in Bengali (the language spoken in Bangladesh). See [HowToUseEktooClient](HowToUseEktooClient.md) for instructions.

![http://mesh4x.googlecode.com/svn/wiki/files/ektoo_1.gif](http://mesh4x.googlecode.com/svn/wiki/files/ektoo_1.gif)

## Source ##
Go to [Source](Source.md) or the Source tab for instructions.

We have rumours contributors are working on Ruby and Javascript and Python. Stay tuned.

### Sync Libraries ###
Various implementations following a unified design across platforms, for [FeedSync](http://feedsync.org) synchronization.

We are implementing in Java, .NET, and have some contributors working on their free time in Ruby and JavaScript versions. While each language may take advantage of different idioms to tackle similar problems, we think that sticking to some common architectural assumptions across implementations will help increase the overall productivity of contributions.


Sync Libraries, Java http://code.google.com/p/mesh4x/source/browse/Mesh4j

Sync Libraries, .NET http://code.google.com/p/mesh4x/source/browse/Mesh4n


## Contributors ##
#### People ####
[InSTEDD Team Blogs](http://www.instedd.org/blogs)

[Eduardo Eduardo Jezierski's (ed) Blog](http://edjez.instedd.org/search/label/Mesh4x)

[Taha Kass-Hout's (taha) Blog](http://kasshout.blogspot.com/search/label/Mesh)

[Daniel Cazzulino's (kzu) Blog](http://www.clariusconsulting.net/kzu/)

[Pablo M. Cibraro's (cibrax) Blog](http://weblogs.asp.net/cibrax/)

[Juan Marcelo Tondato's (jmt) Blog](http://jtondato.clariusconsulting.net/)

_Contribute and add yourself here_


#### Contributing Organizations ####
[InSTEDD](http://instedd.org/) - Contributions will be based on the requirements observed in global health, community development and humanitarian aid.


[Clarius Consulting](http://clariusconsulting.net) - Original development of [FeedSync](http://feedsync.org) .NET libraries. Now working with InSTEDD.

FeedSync is an open protocol licensed under Creative Commons, created by Ray Ozzie and others in the Microsoft Concept Development Team.