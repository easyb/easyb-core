package org.disco.easyb.plugin;

import org.hsqldb.Server
import groovy.sql.Sql
import groovy.xml.MarkupBuilder

given "a database is up and running", {
	//this arg ensures that system.exit() isn't called
	//which was causing issues w/the script
	Server.main(["-no_system_exit", "true"] as String[])
}

and 

given "the database has an initalized data model", {
	sql = Sql.newInstance("jdbc:hsqldb:hsql://127.0.0.1", 
			"sa", "", "org.hsqldb.jdbcDriver")
	ddl = """DROP TABLE definition IF EXISTS;
			 DROP TABLE synonym IF EXISTS;
			 DROP TABLE word IF EXISTS;

			 CREATE TABLE word (
			  WORD_ID bigint default '0' NOT NULL,
			  PART_OF_SPEECH varchar(100) default ''  NOT NULL,
			  SPELLING varchar(100) default '' NOT NULL,
			  PRIMARY KEY  (WORD_ID),
			  UNIQUE (SPELLING));

			 CREATE TABLE definition (
			  DEFINITION_ID bigint default '0' NOT NULL,
			  DEFINITION varchar(500) NOT NULL,
			  WORD_ID bigint default '0' NOT NULL,
			  EXAMPLE_SENTENCE varchar(1000),
			  FOREIGN KEY (WORD_ID) REFERENCES word(WORD_ID) 
			  	ON DELETE CASCADE
			  	ON UPDATE CASCADE,
			  PRIMARY KEY  (DEFINITION_ID));

			 CREATE TABLE synonym (
			  SYNONYM_ID bigint default '0' NOT NULL ,
			  WORD_ID bigint default '0' NOT NULL ,
			  SPELLING varchar(100) default '' NOT NULL ,
			  FOREIGN KEY (WORD_ID) REFERENCES word(WORD_ID) 
			  	ON DELETE CASCADE
			  	ON UPDATE CASCADE,
			  PRIMARY KEY  (SYNONYM_ID));
			 commit;"""	
		sql.execute(ddl);	
}

and 

given "the database_model method is invoked with a dataset", {
	database_model("org.hsqldb.jdbcDriver", 
			  "jdbc:hsqldb:hsql://127.0.0.1", "sa", ""){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);

	builder.dataset(){
		word(word_id:1, spelling:"bellicose", part_of_speech:"Adjective")
		definition(definition_id:10, definition:"demonstrating willingness and willingness to fight ", 
			word_id:1, example_sentence:"The pugnacious youth had no friends left to pick on." )
		synonym(synonym_id:20, word_id:1, spelling:"belligerent") 
		synonym(synonym_id:21, word_id:1, spelling:"aggressive") 
	}
	return writer.toString()
	}
}

when "a select statement is issued for a value that could only be present if the database_model call worked", {
	value = null
	sql.eachRow("select word.spelling from word where word.word_id = 1"){ 
		value = it.spelling
	}
}

then "the word bellicose should be returned", {
	value.shouldBe "bellicose"
}

and 

then "shut down the database", {
	sql.execute("SHUTDOWN")
}