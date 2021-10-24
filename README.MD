Please note: All the relevant files are present under /home/ranglana, <br/>
You can either <br/>
use 'rm -rf lucene-cranfield-app' to delete my git project and clone again and follow steps 1 to 3 <br/>
OR <br/>
jump to step 3 to package the project and run it and use trec_eval on the search results <br/>

1.  SSH into Azure VM (VM name: ranglana)  <br/>
    i. Using Putty and the shared pem or ppk file, login into 20.105.171.105 VM hosted on Azure.

2.  Clone the repository the email id and token shared below <br/>
	i. git clone https://github.com/alwayscommit/lucene-cranfield-app.git <br/>
	ii. username - ranglaniaakash.git@gmail.com <br/>
	iii. shared token - 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 <br/>
	
3.  Run the code <br/>
	cd lucene-cranfield-app/ <br/>
	mvn clean package <br/>
	java -jar target/lucene-cranfield-app-1.jar ../cranfield/cran.all.1400 ../cranfield/cran.qry <br/>
	cd /home/ranglana <br/>
	run ./evaluate.sh (this will run trec_eval on the search output files and save the results in map.txt, precision.txt, recall.txt, gmap.txt) <br/> 
	
