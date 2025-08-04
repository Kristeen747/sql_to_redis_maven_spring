  $(function(){
      getRedis();
      form.unique = $('#checkDefault').is(':checked');
  })

  var form = {};

  function getRedis(){
      $.get( "/redis", function(items) {
            $.each(items, function( i, item){
                $("#redisId").append($('<option>', { text: item.name, value: item.id}));
            })
      })
  }

  $('#redisId').on('change', function() {
   $("#url").empty();
        form.redisId = $('#redisId').val();
        getRedisTables();
  });

   function getRedisTables(){
    $.ajax("/redisTables", {
        data : JSON.stringify(form),
        contentType : 'application/json',
        type : 'POST',
         success: function(items) {
          $("#redisTable").empty();
                                     $.each(items, function( i, item){
                                         $("#redisTable").append($('<option>', { text: item, value: item}));
                                     })
         }

        })
    }

   $('#redisTable').on('change', function() {
    $("#url").empty();
     form.redisTable = $('#redisTable').val();
       getRedisKeys();
     });

     function getRedisKeys(){
             $.ajax("/redisKeys", {
                    data : JSON.stringify(form),
                    contentType : 'application/json',
                    type : 'POST',
                     success: function(items) {
                       $("#redisKey").empty();
                                                 $.each(items, function( i, item){
                                                     $("#redisKey").append($('<option>', { text: item, value: item}));
                                                 })
                     }

               })
        }

      $('#redisKey').on('change', function() {
      form.redisKey = $('#redisKey').val();
            getData();
            createUrl();
      });


        function getData(){
                  $.ajax("/redisData", {
                                     data :JSON.stringify(form),
                                    contentType : 'application/json',
                                     type : 'POST',
                                      success: function(data) {
                                      $("#output").empty();
                                      $("#output").val(data);
                                      }
                                })
        }



     $(document).on("click","#deleteDatabase",function(e) {
        e.preventDefault();
         $.ajax("/deleteDatabase", {
                             data :JSON.stringify(form),
                             contentType : 'application/json',
                             type : 'POST',
                             success: function(data) {
                 getRedisTables();
              }
         })
     })

     $(document).on("click","#deleteKey",function(e) {
        e.preventDefault();
         $.ajax("/deleteKey", {
                             data :JSON.stringify(form),
                             contentType : 'application/json',
                             type : 'POST',
                             success: function(data) {
                 getRedisKeys();
              }
         })
     })

      function createUrl(){
         $("#url").empty();

         let table = $("<table>", {class:'table table-sm'});
         let tr1 = $("<tr>");
         let td1 = $("<td>");

         let tr2 = $("<tr>");
         let td2 = $("<td>");

        let getUrl = window.location.origin + '/api/redis?redisId='+form.redisId + '&redisTable=' + form.redisTable + '&redisKey='+form.redisKey + '&unique=' + form.unique;
        let postUrl = window.location.origin + '/api/redis';

        td1.append(
            $('<a>', {
                      href: getUrl,
                      text : getUrl
                    }
            )
        );

        tr1.append(td1);
        table.append(tr1);

        $("#url").append(
            table
        );
      }

      $('#checkDefault').change(function() {
          form.unique = this.checked;
          createUrl();
      });

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

