rm -rf precision.txt
rm -rf recall.txt
rm -rf map.txt
rm -rf gm_map.txt


for FILE in 'output/'*; 
do
	echo $FILE
	echo $FILE >> precision.txt
	trec_eval/trec_eval -m P ./cranfield/QRelsCorrectedforTRECeval $FILE >> precision.txt
	echo $FILE >> recall.txt
	trec_eval/trec_eval -m recall ./cranfield/QRelsCorrectedforTRECeval $FILE >> recall.txt
	echo $FILE >> map.txt
	trec_eval/trec_eval -m map ./cranfield/QRelsCorrectedforTRECeval $FILE >> map.txt
	echo $FILE >> gm_map.txt
	trec_eval/trec_eval -m gm_map ./cranfield/QRelsCorrectedforTRECeval $FILE >> gm_map.txt
done
