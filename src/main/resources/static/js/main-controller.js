angular.module('uploadAppControllers').controller('MainCtrl', [ '$scope', '$http', function($scope, $http) {
    var vm = this;
    vm.response = {};

    vm.entities = {};

    vm.headers = [
        { "order": 1, "label": "ID", "data": "id", "type": "string", "visible": false },
        { "order": 2, "label": "First Name", "data": "firstName", "type": "string", "visible": true },
        { "order": 3, "label": "Last Name", "data": "lastName", "type": "string", "visible": true },
        { "order": 4, "label": "Age", "data": "age", "type": "number", "visible": true },
        { "order": 5, "label": "Place", "data": "place", "type": "string", "visible": true }
    ];

    vm.fileSelected = function(e) {
        vm.file = e.target.files[0];
        $scope.$apply(function() {
            vm.updateSelectedFileSize();
        });
    };

    vm.updateSelectedFileSize = function() {
        if (vm.file) {
            vm.selectedFileSize = vm.file.size + ' bytes';
        } else {
            vm.selectedFileSize = '<nothing selected>';
        }
    };

    vm.getSavedEntities = function() {
        $http.get('/upload/persons').success(function(response) {
            console.log(response);
            vm.entities = response;
        });
    };

    vm.uploadFile = function() {
        vm.submittedUrl = '/upload/multipartFile';
        var formData = new FormData();
        formData.append('file', vm.file);
        $.ajax({
            url: vm.submittedUrl,
            data: formData,
            cache: false,
            contentType: false,
            processData: false,
            type: 'POST',
            success: function(data) {
                $scope.$apply(function() {
                    vm.errorOccurred = false;
                    vm.uploadSuccessful.call(vm, data);
                });
            },
            error: function(xhr, textStatus, errorThrown) {
                $scope.$apply(function() {
                    vm.errorOccurred = true;
                    vm.error = xhr.responseText;
                    var codeBlock = document.getElementsByClassName('error-json')[0];
                    codeBlock.innerHTML = '';
                    var formattedJson = JSON.stringify(JSON.parse(xhr.responseText), null, 2);
                    codeBlock.appendChild(document.createTextNode(formattedJson));
                    hljs.highlightBlock(codeBlock);
                });
            }
        });
    };
    
    vm.uploadSuccessful = function(data) {
        vm.uploadResponse = JSON.stringify(data);
        vm.response.date = new Date();
        vm.response.fileSizeThreshold = data.fileSizeThreshold;
    };
    vm.updateSelectedFileSize();
}]);