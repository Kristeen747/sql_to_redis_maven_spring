
$(document).on("click","#add_row",function(e) {
    let tr = $('<tr>', { class: 'query-row'});
    //cloned.attr('id', 'clone-box-'+ cloneIndex++);
    let td1 = $('<td>').append($('<select>', { size: 5,
        class: 'form-select',
        id:'rows' + cloneIndex + '.fromDbId',
        name:'rows[' + cloneIndex + '].fromDbId'}).append($('select[id="rows0.fromDbId"] > option').clone()));
    let td2 = $('<td>').append($('<textarea>', { class: 'form-control', rows:10, id:'rows' + cloneIndex + '.query', name:'rows[' + cloneIndex + '].query'}));
    let td3 = $('<td>').append($('<div>', { class: 'btn btn-light btn-sm border rounded-5 delete-row row-btn', text: 'x'}));
    tr.append(td1).append(td2).append(td3);
    $('#queries').append(tr);
});

 $(document).on("click",".delete-row",function(e) {
    $(this).parents("tr").remove();
});

 let cloneIndex = $('.query-row').length;


  $(document).on("click","#test",function(e) {
    e.preventDefault();
    var output = $('#output');
    var form = new FormData($('#form')[0]);
    $('#spinner').show();
           $.ajax({
                 type: 'post',
                 url: '/test',
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
                        var table = $('<table>',{
                                        class:'table table-sm table-light'
                                     });
                         var thead = $('<thead>');
                         var tr = $('<tr>');
                        $.each(data.columnNames, function( i, item){
                            tr.append($("<th>").text(item));
                        });
                        thead.append(tr);
                        table.append(thead);
                        var tbody = $('<tbody>');
                        $.each(data.rows, function( i, item){
                        var tr = $('<tr>');
                            $.each(item, function(j, elem){
                                tr.append($('<td>').text(elem));
                            });
                            tbody.append(tr);
                        });
                        table.append(tbody);

                     output.append(table);
                 }
           }
       })
  });


//  $(document).on("click","#save_form",function(e) {
//      e.preventDefault();
//      var output = $('#output');
//      var form = new FormData($('#form')[0]);
//      $('#spinner').show();
//             $.ajax({
//                   type: 'post',
//                   url: '/save',
//                    data: form,
//                    processData: false,
//                    contentType: false,
//                    cache: false,
//                    success: function (data) {
//             }
//         })
//    });