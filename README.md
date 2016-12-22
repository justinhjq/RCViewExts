# RCViewExts
对RecyclerView特性的一些封装和个性化的支持

## TagsVerticalLayoutManager
垂直方向上的标签流，当标签数量超过一个屏幕时，可以对不可见的标签View进行复用
```Java
 recyclerview.setLayoutManager(new TagsVerticalLayoutManager());
```
