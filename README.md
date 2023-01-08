# android_learn
a private draft android project

#v1.0.1 
Learn how to send data through multi apps.
Send a local file to another app , follow steps:
1.Indicate local file path as FileProvider, so you can use FileProvider get uri.
2.Construct a intent whose action is Intent.ACTION_SEND, and put file uri to Intent.EXTRA_STREAM.
Or maybe you just wanna send a string text, in that case you can put string into Intent.EXTRA_TEXT.
3.Set a right Mime type to Intent.type
4.Indicate destination with app package name and its Activity class name

test push