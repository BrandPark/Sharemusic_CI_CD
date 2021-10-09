/*
We need to register the required plugins to do image manipulation and previewing.
*/
FilePond.registerPlugin(
    FilePondPluginFileValidateType,
    FilePondPluginFileValidateSize,
    FilePondPluginImageExifOrientation,
    FilePondPluginImagePreview,
    FilePondPluginImageCrop,
    FilePondPluginImageResize,
    FilePondPluginImageTransform,
    FilePondPluginImageEdit,
);

// Select the file input and use create() to turn it into a pond
// in this example we pass properties along with the create method
// we could have also put these on the file input element itself
const pond = FilePond.create(
    document.querySelector('.filepond-input'),
    {
            labelIdle: 'Drag & Drop your picture or <span class="filepond--label-action">Browse</span>',
            imagePreviewHeight: 170,
            imageCropAspectRatio: '1:1',
            imageResizeTargetWidth: 200,
            imageResizeTargetHeight: 200,
            imagePreviewMaxFileSize: '3MB',
            acceptedFileTypes: ['image/jpg', 'image/png'],
            stylePanelLayout: 'compact circle',
            styleLoadIndicatorPosition: 'center bottom',
            styleProgressIndicatorPosition: 'right bottom',
            styleButtonRemoveItemPosition: 'center bottom',
            styleButtonProcessItemPosition: 'right bottom',
    }
);

$.fn.filepond.setDefaults({
    maxFileSize: '3MB',
    labelMaxFileSize: '파일의 크기가 너무 큽니다. 3MB 이하의 사진을 사용해 주세요',
    fileValidateTypeLabelExpectedTypes: 'image/jpeg, image/png 형식의 파일만 가능합니다.',
});

pond.on('addfile', function(_error, _file){
    if(_error){
        alert(error['sub']);
        pond.removeFile(_file);
        return;
    }
});