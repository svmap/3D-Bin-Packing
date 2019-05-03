'use strict';

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');



function uploadMultipleFiles(files) 
{
    var formData = new FormData();
    for(var index = 0; index < files.length; index++) 
    {
        formData.append("files", files[index]);
    }
    var authorize ="";
	if($.session.get("Authorization") != null)
	{
		authorize = $.session.get("Authorization");
	}
	var header = {"Authorization":authorize,"Content-Type": "multipart/form-data"};
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadMultipleFiles");
    xhr.setRequestHeader("Authorization",authorize);
    xhr.onload = function() 
    {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) 
        {
            multipleFileUploadError.style.display = "none";
            var content = "<p>All Files Uploaded Successfully</p>";
            for(var i = 0; i < response.length; i++) 
            {
                content += "<p>DownloadUrl : <a class='btn btn-info' href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
            }
            content += "<p>DownloadUrl : <a class='btn btn-info' href='http://139.59.61.78:2020/downloadFile/output.json' target='_blank'>OUTPUT JSON</a></p>";
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } 
        else 
        {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(formData);
}


multipleUploadForm.addEventListener('submit', function(event)
{
    var files = multipleFileUploadInput.files;
    if(files.length === 0) 
    {
        multipleFileUploadError.innerHTML = "Please select at least one file";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files);
    event.preventDefault();
}, true);

