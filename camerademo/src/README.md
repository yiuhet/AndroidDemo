## 调起相机应用
```
    //调起相机应用
    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, 0);
    //获取返回数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ((ImageView)
    }
    
```

## Camera（弃用）
>Camera类已被弃用，官方建议使用更新的 android.hardware.camera2。

### 基本流程
检测和开启相机  
创建一个预览来显示相机图像  
设置相机基本参数  
设置拍照/录像监听  
文件保存  
释放相机资源  



## Camera2

### 基本流程


