import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
  
col_names=['Analyzer Similarity Pair', 'mAP', 'gm_mAP']
output_data = pd.read_csv('output.csv', header=None, names=col_names)
  
df = pd.DataFrame(output_data)
ind = np.arange(13)
labels = df.iloc[:, 0]  
map = df.iloc[:, 1]
print(map.shape)
gmap = df.iloc[:, 2]

fig=plt.figure(figsize=(10,8))

ax = plt.subplot(111)
width = 0.35
ax.bar(ind, map, width, color='blue', label="mAP Score")
ax.bar(ind+width, gmap, width, color='yellow', label="gm_mAP Score")
ax.legend(loc='upper right', bbox_to_anchor=(1.1,1.1))

plt.xticks(ind + width / 2, ('0', '1', '2', '3', '4', '5', '6','7', '8', '9','10', '11', '12'))

#stack overflow reference - https://stackoverflow.com/questions/62941033/how-to-turn-x-axis-values-into-a-legend-for-matplotlib-bar-graph
x_legend = '\n'.join(f'{index} - {analyzer_similarity}' for index,analyzer_similarity in zip(ind,labels))
t = ax.text(.63,.4,x_legend,transform=ax.figure.transFigure, color='blue')
plt.subplots_adjust(right=.6)

plt.xlabel('Analyzer and Similarity Pairs')
plt.ylabel('trec_eval Score')
plt.title('Search Engine Evaluation')

plt.show()