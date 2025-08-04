  $(function(){
      getTasks();
  })
//
//  var form = {};
//
  function getTasks(){
      $.get( "/tasks/csv", function(items) {
            $("#tasks").empty();

            var table = $('<table>',{
                                    class:'table table-sm table-light'
                                    });
           var thead = `
           <thead>
                <tr>
                    <th></th>
                    <th>Filename</th>
                    <th>AddedAt</th>
                    <th>Redis</th>
                    <th>RedisTable</th>
                    <th>RedisKey</th>
                </tr>
           </thead>`;

           var tbody = $('<tbody>');

            $.each(items, function( i, item){
                 let downloadLink = $('<a>', { class: 'link', 'data-filename': item.filename , text: item.filename, href: window.location.origin + '/download/' + item.filename});
                 var tr = $('<tr>');
                 var td1 = $('<td>').append($('<div>', {class: 'btn btn-sm rounded-5 delete', 'data-id': item.id})
                 .append(`<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x" viewBox="0 0 16 16">
                 <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"></path>
                 </svg>`));
                 var td2 = $('<td>').append(downloadLink);
                 var td3 = $('<td>').append(item.addedDate);
                 var td4 = $('<td>').text(item.redisName);
                 var td5 = $('<td>').text(item.redisTable);
                 var td6 = $('<td>').text(item.redisKey);

                 tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6);
                 tbody.append(tr);
           });
            table.append(thead).append(tbody);
            $("#tasks").append(table);
            })
  }

$(document).on("click",".delete",function(e) {
   let id = $(this).data("id");
   $.get("/tasks/csv/" + id + "/delete", function() {
         getTasks();
    })
  })

   $(document).on("click",".link",function(e) {
    let filename = $(this).data("filename");

     $.get(getUrl, function() {
     })
   })

      $(document).on("click","#test",function(e) {
          e.preventDefault();
          var output = $('#output');
          var form = new FormData($('#form')[0]);
          $('#spinner').show();
                 $.ajax({
                       type: 'post',
                       url: '/api/redis',
                        data: form,
                        processData: false,
                        contentType: false,
                        cache: false,
                        success: function (data) {
                        output.empty();
                        $('#spinner').hide();
                        if(data.errorMessage !== null && data.errorMessage!==""){
                              var textarea = $('<textarea>',{
                                                     class:'form-control',
                                                     type: 'text',
                                                     text: data.errorMessage
                                               });
                              output.append(textarea);
                          }else{
                             var textarea = $('<textarea>',{
                                                            class:'form-control',
                                                            type: 'text',
                                                            text: data.errorMessage
                                                           });
                           output.append(textarea);
                       }
                       getTasks();
                 }
             })
        });

