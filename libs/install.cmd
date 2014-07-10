# mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>
mvn install:install-file -Dfile=prefuse.jar -DgroupId=prefuse -DartifactId=prefuse -Dversion=2007.1 -Dpackaging=jar
mvn install:install-file -Dfile=layouts.jar -DgroupId=com.jhlabs -DartifactId=jhlayouts -Dversion=20110707 -Dpackaging=jar
mvn install:install-file -Dfile=GlazedListsAux.jar -DgroupId=net.java.dev.glazedlists -DartifactId=aux -Dversion=1.8.0 -Dpackaging=jar