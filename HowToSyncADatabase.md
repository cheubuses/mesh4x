#How to sync with a database with Mesh4x

# Introduction #
This howto shows how a developer can synchronize your database with other repositories using mesh4x. For this example we will use MySQL as one endpoint, and assume you want to synchronize via HTTP to a server

You will need:
  * A way of connecting to your database
  * Your database schema
  * Basic knowledge about Java, Eclipse and Hibbernate


# Overview #
The process consists of:
  1. Creating a Hibernate map for your table
  1. Creating or modifying the user interface for your application
  1. Instantiating the mesh4j libraries with the right adapters
  1. Triggering the synchronization

# Details #
See HowToUseHibernateAdapater for more information.

# Frequently Asked Questions #