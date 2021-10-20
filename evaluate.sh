for FILE in *; do echo -e "$FILE\nLoops Rule\!" > $FILE; done

./trec_eval -m map ../cranfield/cranqrel ../lucene-cranfield-app/


java -jar target/lucene-cranfield-app-1.jar /home/ir/cranfield/cran.all.1400 /home/ir/cranfield/cran.qry