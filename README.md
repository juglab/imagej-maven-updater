# ImageJ Maven Updater / Installer

This is a prototype demonstrating how to (partially) build and update ImageJ via Maven.

## How to use this prototype
```
mvn compile
mvn exec:java -Dexec.mainClass=net.imagej.mavenupdater.MavenInstaller
```
After the installation is done, adjust the path to your installation directory [here](https://github.com/juglab/imagej-maven-updater/blob/master/src/main/java/net/imagej/mavenupdater/MavenUpdater.java#L97), recompile and call the updater separately:
```
mvn exec:java -Dexec.mainClass=net.imagej.mavenupdater.MavenUpdater
```

Keep in mind that **you cannot change your installation using this tool while the installation is running**. This is an external tool (and I think it should be).

## What's the idea?

Update sites no longer use an ImageJ-specific format, but are Maven artifacts.

Here are example used in this prototype:
  - [updatesite-imagej](https://github.com/juglab/updatesite-imagej)
  - [updatesite-ij1](https://github.com/juglab/updatesite-ij1])
  - [updatesite-fiji](https://github.com/juglab/updatesite-fiji)

The Maven installer builds a `pom.xml` representing the local installation ([template](src/main/resources/pom-template.xml)), puts it into Fiji.app and adds the chosen update sites as dependencies. From there, the installation is build by the following Maven command ([in Java though]()):
```
mvn -Dscijava.app.directory=/YOUR/LOCAL/Fiji.app -Ddelete.other.versions=true -Denforcer.skip=true install
```

### How are non-JARs handled?

Non-JAR binaries are included into the Maven projects by adding them as a resource. I created a [branch including all reosurces](https://github.com/juglab/fiji/tree/including-resources) of the `fiji/fiji` repository demonstrating how to do that, have a look at [this part](https://github.com/juglab/fiji/blob/including-resources/pom.xml#L901-L930) of the POM:
```
<resources>
  <resource>
    <directory>luts</directory>
    <targetPath>Fiji.app/luts</targetPath>
  </resource>
  <resource>
    <directory>macros</directory>
    <targetPath>Fiji.app/macros</targetPath>
  </resource>
  <resource>
    <directory>Contents</directory>
    <targetPath>Fiji.app/Contents</targetPath>
  </resource>
  <resource>
    <directory>images</directory>
    <targetPath>Fiji.app/images</targetPath>
  </resource>
  <resource>
    <directory>plugins</directory>
    <targetPath>Fiji.app/plugins</targetPath>
  </resource>
  <resource>
    <directory>resources</directory>
    <targetPath>Fiji.app/resources</targetPath>
  </resource>
  <resource>
    <directory>scripts</directory>
    <targetPath>Fiji.app/scripts</targetPath>
  </resource>
</resources>
```
Notice that I added `Fiji.app` to the beginning of the target path. This is used to signal that these are resources which should be unpacked into Fiji. This is done on [this scijava-maven-plugin branch](https://github.com/juglab/scijava-maven-plugin/tree/extract-resources), it contains a new Mojo one can call from Maven via ` mvn -Dscijava.app.directory=/YOUR/LOCAL/Fiji.app scijavaclone:extract-resources`.

### How does one update an update site?
The idea (and this might not work because of [this issue](https://stackoverflow.com/questions/45041888/how-can-i-depend-on-a-library-with-transitive-dependencies-which-are-adjusted-by)) is that the update sites determine the dependencies and the specific versions will be set by the `pom-scijava` parent of your local installation POM. Update sites could also name specific versions if they need to. There are further tests needed to determine if Maven does what we need in this scenario.

#### Releases vs. snapshots
The updater would look for new versions of `pom-scijava` and one there is a new version, rebuild the application. The user could also enable a snapshot mode where the application would also update in case there is a new `pom-scijava` snapshot version to get something like a nightly build.

#### If your update site project is maven maintained
In this case you would probably add the version of your project to the `pom-scijava` BOM and update it there. If you don't want that, your update site POM would point to a specific version of your plugin etc.

This requires that people can easily deploy their project to a maven server. This is not yet possible. For this prototype we have this maven test repository where anyone can upload to:
```
<repository>
  <id>test-maven</id>
  <name>DAIS Maven test repository</name>
  <url>https://dais-maven.mpi-cbg.de/repository/test-maven/</url>
</repository>
```

#### If your update site project is not maven maintained
In this case the ImageJ uploader chould build a POM including all files you added to ImageJ as resources and upload that to our Maven server. This is not implemented at all yet. It could also be used to migrate existing update sites in the known format into Maven update sites.

## So can you build a whole Fiji from Maven?
No.
The installer will ask you for a working ImageJ installation at the beginning and will then delete all subfolders except `java/`. This keeps the JRE and the executable in place, this prorotype cannot get/build them.

## Why are there so many GIT commands printed to the console?
In the background I use JGIT to manage the files / compare the local installation and an updated installation etc.. I'm sure there is a better (and more performant) way to do this, but since I wrote the GIT part earlier with a different use case in mind, this was the easiest to just use for this prototype.

## What about local changes to the installation?
One would have to track what came via Maven (ideally from which dependency) to recognize locally modified /  changed files. The current GIT versioning can do that to some extend (not the dependency source part), but I might not find time to work on in this early prototype stage / before we know if the whole project will lead somewhere.

## Development resources
Here are all the repositories I messed with to make this prototype work:
- [Main Maven Updater project](https://github.com/juglab/imagej-maven-updater)
- [Fiji repo including resources](https://github.com/juglab/fiji/tree/including-resources)
- [scijava-maven-plugin-clone extracting resources](https://github.com/juglab/scijava-maven-plugin/tree/extract-resources)
- [updatesite-imagej](https://github.com/juglab/updatesite-imagej)
- [updatesite-ij1](https://github.com/juglab/updatesite-ij1])
- [updatesite-fiji](https://github.com/juglab/updatesite-fiji)
