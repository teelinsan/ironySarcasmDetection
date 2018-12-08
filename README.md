# A Kernel-based Approach for Irony and Sarcasm Detection in Italian

This is the code's repository of the paper "A Kernel-based Approach for Irony and Sarcasm Detection in Italian" presented at <a href="http://www.di.unito.it/~tutreeb/ironita-evalita18/">IronITA @ Evalita2018</a>

The system ranked **first** and **second** at the sarcasm detection task, while it ranked sixth and seventh at the irony detection task.

Please if you use this code cite:
```
Bibitex
```

## Usage
The jupyter notebook file "Genera file Kelp" contains all the python code used to model the features for the task.

### Prerequisites
In order to use this code you have first to download all the files in the repository, the <a href="http://www.di.unito.it/~tutreeb/ironita-evalita18/data.html">datasets for the task </a> and place them in the same folder.

Once you have downloaded these data, you need to preprocess them with a POS-tagger and lemmatizer.

At the end you have to generate a copy of each dataset downloaded `test_dataset.klp -> ../test_ironita2018_revnlt_processed.tsv` `train_dataset.klp  -> ../training_ironita2018_renlt_processed.tsv ` add a new column in the datasets 


The .pickle files contain the words frequency extracted from the Irony Corpus.

1. You have to download all the files in the repository, the <a href="http://www.di.unito.it/~tutreeb/ironita-evalita18/data.html">datasets for the task </a> and put them in the same folder.
2. Then you have to run the "Genera file Kelp" jupyter notebook to generate the .klp file with the features.
3. At the end you have to use <a href="http://www.kelp-ml.org/">KeLP</a> to use the modelled features in a kernel machine.
