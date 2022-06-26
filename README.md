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

#learn_layoutinflater
1.了解了LayoutInflater.inflate(resource, @Nullable ViewGroup root, boolean attachToRoot)方法的使用，
root 在不为空时，resource布局中的layout_xxx等attributes才会生效，这是因为layout_xxx是父布局对子布局的约束。
而attachToRoot，则决定了是否将resource生成的view加入到root中。
通常在我们只想获取一个View时，attachToRoot可以传false，再自行决定如何将其添加。
在Recyclerview中使用时，adapter创建ViewHolder时，创建的itemView，则attachToRoot需要false，这是Recyclerview自己定义的规则，
因为Recyclerview会自行控制View的添加和移除（为了效率复用View）
2.了解了Recyclerview的大致运行结构。Recyclerview作为父布局，LayoutManager作为布局管理，Adapter作为数据和View之间的
适配器。Recyclerview只会将当前展示的View添加到Recyclerview中，不展示的则移除。


#学习自定义layoutManager
资料：
1.https://wiresareobsolete.com/2014/09/building-a-recyclerview-layoutmanager-part-1/
2.https://github.com/devunwired/recyclerview-playground
