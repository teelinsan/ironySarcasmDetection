# A Kernel-based Approach for Irony and Sarcasm Detection in Italian

This is the code's repository of the paper "A Kernel-based Approach for Irony and Sarcasm Detection in Italian" presented at <a href="http://www.di.unito.it/~tutreeb/ironita-evalita18/">IronITA @ Evalita2018</a>

The system ranked **first** and **second** at the sarcasm detection task, while it ranked sixth and seventh at the irony detection task.

Please if you use this code cite:
```
Bibitex
```

## Usage
This repository contains the jupyter notebook file `GenerateKLPFile` used to model the features for the task as explained in the paper.

It also contains the `.pickle` files with the words frequency extracted from the Irony Corpus.

### Prerequisites
1. In order to use this code you have first to download all the files in the repository, the <a href="http://www.di.unito.it/~tutreeb/ironita-evalita18/data.html">datasets for the task </a> and place them in the same folder.

2. Once you have downloaded these data, you need to preprocess them with a POS-tagger and lemmatizer. You have to generate a copy of each dataset downloaded   ```test_dataset.klp -> ../test_ironita2018_revnlt_processed.tsv``` ```train_dataset.klp  -> ../training_ironita2018_renlt_processed.tsv ```  and put them in the parent directory `../` . These 2 new copy of the datasets that you have to generate have an extra column `text::text::S` where, for each line, the tex has been preprocessed as `text::lemma::POS`

3. Now you can use the jupyter notebook file `GenerateKLPFile` to generate the .klp file with all the features modelled as explained in the paper.

4. At the end you have to use <a href="http://www.kelp-ml.org/">KeLP</a> to use the modelled features in a kernel machine (linear combination, as explained in the paper, or other type of combination/kernel). An example of kelp classification can be found in the file `IroniTAClassifier.java`
