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

### EXTRecyclerAdapter/MultiType 封装了RecyclerView.Adapter
```Java
EXTRecyclerAdapter<String> adapter = new EXTRecyclerAdapter<String>(R.layout.item_tag_view) {
            @Override
            public void onBindViewHolder(EXTViewHolder holder, int position, String data) {
                holder.setText(R.id.text, data);
            }
        };
```
