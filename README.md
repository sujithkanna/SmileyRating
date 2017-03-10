# Smiley Rating
SmileyRating is a simple rating bar for android. It displayes animated smileys as rating icon.
  - Drawn completely using android canvas
  - Inspired by [Bill Labus](https://dribbble.com/shots/2790473-Feedback)

## Demo

 <img src="https://raw.githubusercontent.com/sujithkanna/SmileyRating/master/app/src/main/assets/demo.gif" alt="" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="575" height="205" />

## Integration
Integrating SmileyRating in your project is very simple.
### Step 1:
Add this dependency in your project's build.gradle file which is in your app folder
```groovy
compile 'com.github.sujithkanna:smileyrating:1.4.1'
```
add this to your dependencies.
## Step 2:
Now place the SmileyRating in your layout.
###### *Note: The height of the SmileyRating will be automatically adjusted according to the width of this component.*
```xml
<com.hsalf.smilerating.SmileRating
        android:id="@+id/smile_rating"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```
## Step 3:
### Initialize your view
```java
SmileRating smileRating = (SmileRating) findViewById(R.id.smile_rating);
```
### Set this SmileySelectionListener to get notified when user selects a smiley
```java
smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley) {
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        break;
                }
            }
        });
```
### If you want to know the level of user rating, You can listen for OnRatingSelectedListener
```java
smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int level) {
                // level is from 1 to 5
            }
        });
```
### You can set selected smiley without user interaction
#### Without animation
```java
smileRating.setSelectedSmile(BaseRating.GREAT);
```
#### OR
```java
smileRating.setSelectedSmile(BaseRating.GREAT, false);
```
*The smiley will be selected without any animation and the listeners won't be triggered*
#### With animation
```java
smileRating.setSelectedSmile(BaseRating.GREAT, true);
```
*Smiley will be selected with animation and listeners will also be triggered(Only if the second param is true)*
#### You can change the smiley name also
```java
mSmileRating.setNameForSmile(BaseRating.TERRIBLE, "Angry");
```
![Angry](https://raw.githubusercontent.com/sujithkanna/SmileyRating/master/app/src/main/assets/angry.jpg)