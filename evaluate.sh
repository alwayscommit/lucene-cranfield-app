for FILE in *; do echo -e "$FILE\nLoops Rule\!" > $FILE; done

./trec_eval -m map ../cranfield/cranqrel ../lucene-cranfield-app/