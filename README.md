# 4413Project

This README is also available on [Github](https://github.com/EthanRei/4413Project)\
The Frontend react source code can be found under the FRONTEND folder.
### Integrating the front-end with springboot
- install node js
- navigate to the FRONTEND directory
- run npm install
- run npm run build
- copy all files in the generated build folder in the FRONTEND directory to the springboot static folder
- remove /static/ from the index.html file
- move index.html, asset-manifest.json, and manifest.json inside the static folder

### Running the project via docker image
Alternatively can pull the image through `docker pull polejimyahoo/project4413:3` [Docker Hub Repo](https://hub.docker.com/layers/polejimyahoo/project4413/3/images/sha256-126b571038d6598bef028217894412158c17e9059a53e6c8d0b136beca559530)\
The command used to run the image is `docker run -p 8080:8080 polejimyahoo/project4413:3`

### Running the project locally
- Have java and jdk installed (atleast version 17 but worked on 23)
- Have maven installed
- Run `mvn install`
- Run the jar file in the target folder through `java -jar target/Project4413-0.0.1-SNAPSHOT.jar`

### Admin credentials
User: admin\
Pass: 123
