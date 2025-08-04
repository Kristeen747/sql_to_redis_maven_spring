
$('select#type').on('change', function() {
  getData(this.value);
});

function getData(type){
    $.get( "/type/" + type, function(items) {
        console.log("type");
    })
}

   $(document).on("click",".clone",function(e) {
        e.preventDefault();
        var id = $(this).data("id");
       $.post('/clone', { 'id': id},
               function(tasks){
                    createTasks();
           });
      })