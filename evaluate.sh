echo "'hello', 'world'" >> log.csv
for FILE in *; do echo -e "$FILE\nLoops Rule\!" > $FILE; done
