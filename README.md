complement-naive-bayes
======================

Implementation of Complement Naive Bayes text classifier used for automatic categorisation of [DaWanda](http://dawanda.com) products.
Complement Navie Bayes was chosen over the classic Naive Bayes due to the fact that distribution of products among
categories tend to be _skewed_ (more products in one category than another), which causes Classic Navie Bayes to
prefer categories which had more products during the training phase. Complement Navie Bayes performs much better
for skewed data.

## Usage
*complement-naive-bayes* might be used as a library which exposes API for traning and labeling of new products 
or as a standalone command line application.
### Command line interface
In order to use *complement-naive-bayes* from command line:
* clone the repo:
```
git clone 
```
* go to project dir and create executable jar
```
cd complement-naive-bayes
./gradlew jar
```
* invoke **java -jar complement-naive-bayes-{version}.jar** to see the options:
```
The following option is required: -c, --command
Usage: <main class> [options]
  Options:
  * -c, --command
       Command for the classifier, can be 'train' for training, 'label' for
       label assignment, or 'validate' for validating the classifier accuracy
    -o, --outputModel
       Output file for the model. Option valid only for training.
       Default: ~/.cbayes/model.json
    -te, --testDir
       Input directory containing product files for labeling/validation
       Default: ~/.cbayes/test
    -tr, --trainDir
       Input directory containing product files for training
       Default: ~/.cbayes/train
```
* put your JSON train product files in _trainDir_ and train your model:
```
java -jar complement-naive-bayes-{version}.jar -c train --trainDir trainDir
```
* put your JSON test product files in _testDir_ and validate you model:
```
java -jar complement-naive-bayes-{version}.jar -c validate --testDir testDir
```
* put your JSON product files that you want to label in _testDir_ and label you products:
```
java -jar complement-naive-bayes-{version}.jar -c label --testDir testDir
```
#### JSON product files
*trainDir*/*testDir* must contain product files in JSON format. Each file must contain list of products with
the following JSON schema:
```
[
    {
        "id": 1,
        "sellerId": 12,
        "category": 123,
        "title": "test title1",
        "description": "test description1"
    },
    {
        "id": 2,
        "sellerId": 23,
        "category": 123,
        "title": "test title2",
        "description": "test description2"
    }
]
```
* For training _categoryId, title, description, sellerId_ attributes are obligatory, _sellerId_ is needed to filter
products of the same seller from a given category in order to avoid _Seller Bias_.
* For testing only _title, description_ attributes are necessary.
* For now only English language is supported, but it's very easy to add support for other languages, all one has
to do is create _Tokenizer_ for a given language and train the model using this _Tokenizer_.

### API
The follwing snippet of code show how to use already trained model in order to label a sample product:
```
// read Naive Bayes model from JSON file
String pathToModel = "./model.json";
NaiveBayesModel model = NaiveBayesSerializer.readFrom(pathToModel);

// create Complement Naive Bayes classifier
DocumentClassifier classifier = new WeightNormalizedComplementNaiveBayes(model);

// get the title and description of the product which is to be labeled
String title = "...";
String description = "...";
String text = title + " " + description;

// extract features, MAKE SURE THE SAME EXTRACTOR WAS USED DURING TRAINING PHASE
Document document = Extractors.STANDARD_EXTRACTOR.extractFeatureVector(text);

// label document
LabelingResult labelingResult = classifier.label(document);

// get categories ordered by score
List<LabelingResult.ScoredCategory> categories = labelingResult.getOrderedCategories();

// print 3 best category suggestions according to the model
System.out.println(Lists.newArrayList(Iterables.limit(categories, 3)));
```
... or use [the following Play application](https://bitbucket.org/lfundaro/classifier-services)
