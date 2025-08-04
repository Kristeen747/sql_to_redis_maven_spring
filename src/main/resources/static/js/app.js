

var groups = [];
var tasks = {};


function groupByKey(array, key) {
   return array
     .reduce((hash, obj) => {
       if(obj[key] === undefined) return hash;
       return Object.assign(hash, { [obj[key]]:( hash[obj[key]] || [] ).concat(obj)})
     }, {})
}

$(function(){
    createTasks();
    setInterval(updateTasks, 1*1000);
})

function getTasks(){
    $.get( "/tasks", function(items) {
        tasks = items;
    })
}

function updateTasks(){
    $.get( "/update-tasks", function(items) {
            $.each(items, function( i, tasks){
                         $.each(tasks, function( j, item){
                            $(".last-run[data-id='" + item.id + "']").text(item.lastRun);
                            $(".last-exec-time[data-id='" + item.id + "']").text(item.lastExecTime + 's');
                            $(".last-rows[data-id='" + item.id + "']").text(item.lastRows);
                           let lastStatus = $(".last-status[data-id='" + item.id + "']");
                           lastStatus.removeClass();
                           lastStatus.addClass('last-status tag badge');
                           lastStatus.addClass(item.status);
                           lastStatus.text(item.status);
                         })
            })
     })

}


function createTasks(){
$.get( "/tasks", function(items) {
 var groups = $('#tasks');
     groups.empty();
     $.each(items, function( i, tasks){
             console.log();


               var group = $('<div>',{
                                    class:'group'
                                     });
                var groupHeader = $('<div>',{
                                             class: 'group-header',

                                    });
                var header = $('<h5>',{
                                       text: i
                                     });
                var desc = $('<small>', {
                                            class: 'm-2',
                                            text: '(' + tasks.length + ' tasks)'
                                       });
               header.append(desc);
               var groupBtn = $('<button>', {
                                                class: 'btn btn-sm btn-light border run-group',
                                                text: 'Run Group',
                                                'data-id': i
                                            });
               groupHeader.append(header).append(groupBtn);
               group.append(groupHeader);

               $.each(tasks, function( j, item){
                    group.append(createSqlTask(item));
                 })
                 groups.append(group);
            })
            })
}

function createSqlTask(item){
    let task = $('<div>',{
                          class:'task row' // Add .row class
                         }).attr('data-id',item.id);

                        let leftInfo =  $('<div>', { class:'col-6'}); // Use col-6 for left side
                        let taskType = $('<span>', {class: 'tag badge ' + item.taskType, text: item.taskType});

                        let settings = $('<div>', {
                                               class:'settings mt-2'
                                     });

                         let tags1 = $('<div>', {
                                               class:'tags'
                                     });

                        let name = $('<a>',{
                                                   class:'name d-block', // d-block for better layout
                                                   text: item.redisKey,
                                                   href:'/editSqlTask/' + item.id
                                                  });

                         let id = $('<span>', { class: 'tag'})
                                                          .append($('<span>', { class: 'label', text: 'id'}))
                                                          .append($('<span>', { class: 'value', text: item.id }));

                         let fromDb = $('<span>', { class: 'tag'})
                                                    .append($('<span>', { class: 'label', text: 'fromDb'}))
                                                    .append($('<span>', { class: 'value', text: item.fromDbName }));

                         let toDb = $('<span>', { class: 'tag'})
                                                    .append($('<span>', { class: 'label', text: 'toDb'}))
                                                    .append($('<span>', { class: 'value', text: item.redisName }));
                        let redisTable = $('<span>', { class: 'tag'})
                                                                       .append($('<span>', { class: 'label', text: 'redisTable'}))
                                                                       .append($('<span>', { class: 'value', text: item.redisTable }));

                        tags1.append(id).append(fromDb).append(toDb).append(redisTable);
                        settings.append(name).append(tags1);
                        leftInfo.append(taskType).append(settings)

                        let rightInfo = $('<div>', { class:'col-6 d-flex flex-column align-items-end'}); // col-6 for right side and flex for alignment

                        let tags = $('<div>', {
                                               class:'tags',
                                               'data-id': item.id
                                      });

                        let tag1 = $('<span>', { class: 'tag'})
                                .append($('<span>', { class: 'label', text: 'LastRun'}))
                                .append($('<span>', { class: 'value last-run', 'data-id': item.id, text: item.lastRun }));

                        let tag2 = $('<span>', {class: 'tag'})
                                .append($('<span>', {class: 'label', text: 'Rows'}))
                                .append($('<span>',{ class: 'value last-rows', 'data-id': item.id,text: item.lastRows}));

                        let tag3 = $('<span>', {class: 'tag'})
                                   .append($('<span>', {class: 'label', text: 'ExecTime'}))
                                   .append($('<span>', {class: 'value last-exec-time', 'data-id': item.id, text: item.lastExecTime +'s'}));

                        let statusBadge = $('<span>', {class: 'tag badge last-status ' + item.status, 'data-id': item.id,  text: item.status});



                        tags.append(tag1).append(tag2).append(tag3).append(statusBadge);

                        var btnGroup = $('<div>',{
                                        class:'btn-group mt-2'
                                    });
                        var startBtn = $('<div>',{
                                         class:'btn btn-sm btn-light border start'
                                     }).append(
                                        '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-play-fill" viewBox="0 0 16 16"><path d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z"/></svg>'
                                     ).attr('data-id',item.id );

                         var cloneBtn= $('<div>',{
                                               class:'btn btn-sm btn-light border clone'
                                        }).append(
                                                         '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus" viewBox="0 0 16 16"><path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4"/></svg>'
                                                         ).attr('data-id',item.id );
                       var deleteBtn= $('<div>',{
                                                              class:'btn btn-sm btn-light border delete'
                                                       }).append(
                                                                        '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x" viewBox="0 0 16 16"><path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/></svg>'
                                                                        ).attr('data-id',item.id );
                        btnGroup.append(startBtn).append(cloneBtn).append(deleteBtn);
                        rightInfo.append(tags).append(btnGroup);
                        task.append(leftInfo).append(rightInfo);
                         return task;
}


$(document).on('click', '.start', function(e) {
  e.preventDefault();
    var id = $(this).data("id");
   $.post('/start', { 'id': id},
       function(data){

       }
   );
});

   $(document).on("click",".clone",function(e) {
        e.preventDefault();
        var id = $(this).data("id");
       $.post('/clone', { 'id': id},
               function(tasks){
                    createTasks();
           });
      })

 $(document).on("click",".delete",function(e) {
        e.preventDefault();
        var id = $(this).data("id");
        console.log(id);
       $.post('/delete', { 'id': id},
               function(tasks){
                    createTasks();
           });
      })

  $(document).on("click",".run-group",function(e) {
          e.preventDefault();
          var id = $(this).data("id");
         $.post('/api/run/group/' + id,
                 function(tasks){
                      console.log("run group");
             });
        })

