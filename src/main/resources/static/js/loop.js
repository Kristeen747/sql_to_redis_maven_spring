//  $(function(){
//      getRedis();
//  })
//
//  var form = {};
//
//  function getRedis(){
//      $.get( "/redis", function(items) {
//            $.each(items, function( i, item){
//                $("#redisId").append($('<option>', { text: item.name, value: item.id}));
//            })
//      })
//  }

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
                 }
             })
        });

