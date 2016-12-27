# RCViewExts
对RecyclerView特性的一些封装和个性化的支持

### LayoutManager
* TagsVerticalLayoutManager
<br>垂直方向上的标签流，当标签数量超过一个屏幕时，可以对不可见的标签View进行复用
```Java
 recyclerview.setLayoutManager(new TagsVerticalLayoutManager());
```
* TagsHorizontalLayoutManager
<br>水平方向上的标签流，当标签数量超过一个屏幕时，可以对不可见的标签View进行复用
```Java
 recyclerview.setLayoutManager(new TagsHorizontalLayoutManager());
```
* TagsAdapter
<br>基于EXTRecyclerAdapter扩展的适用于TagsLayoutManager，标签流的数据适配器
```Java
TagsAdapter adapter;
adapter.setMode(mode); // 设置选中模式 SingleChoice, Multichoice, None
adapter.getSelectedPosition(); // 获取选中的位置
adapter.getSelectedPositions();// 获取选中的位置
```
### ItemTouchHelper、ItemTouchHelper.Callback
* SimpleDragSwapCallback
<br>拖拽替换ItemView，同时替换相应的数据源
```Java
ItemTouchHelper helper = new ItemTouchHelper(new SimpleDragSwapCallback(adapter));
helper.attachToRecyclerView(recycler_view);// helper生效
```
* SimpleSwipeCallback
<br>滑动删除ItemView，同时删除相应的数据源
```Java
ItemTouchHelper helper = new ItemTouchHelper(new SimpleSwipeCallback(adapter));
helper.attachToRecyclerView(recycler_view);// helper生效
```
* CycleAlbumCallback、CycleAlbumLayoutManager
<br>循环大图相册

### EXTRecyclerAdapter/MultiType 封装了RecyclerView.Adapter
```Java
EXTRecyclerAdapter<String> adapter = new EXTRecyclerAdapter<String>(R.layout.item_tag_view) {
            @Override
            public void onBindViewHolder(EXTViewHolder holder, int position, String data) {
                holder.setText(R.id.text, data);
            }
        };
```
