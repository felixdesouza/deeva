var type_array = 'ARRAY';
var type_string = 'STRING';
var type_object = 'OBJECT';
var empty_object = {
    value: undefined
};

var visible_objects = [];
var all_objects = [];

// Primitive types in Java.
var primitive_list = ["int", "char", "boolean", "byte", "float", "double", "long", "short"];

function setJsPlumbDefaults() {
    jsPlumb.importDefaults({
        ConnectionsDetachable: false
    });
    jsPlumb.Defaults.Container = "visual";
    jsPlumb.Defaults.Endpoints = [
        ["Dot", {radius: 5, cssClass: "stackPoint" }],
        ["Blank", {radius: 6}]
    ];
    jsPlumb.Defaults.Anchors = [
        [0.5, 0.5, 1, 1],
        "Left"
    ];
    jsPlumb.Defaults.ConnectionOverlays = [
        ["Arrow", {
            width: 6,
            length: 6,
            location: 1
        }],
    ];

    jsPlumb.Defaults.Connector = ["StateMachine"];
}

function main(all_variables) {
    setJsPlumbDefaults();

    d3.selectAll("#global_area").remove();
    d3.selectAll("#heapBody").remove();
    d3.selectAll("#stackFrames").remove();

    jsPlumb.deleteEveryEndpoint();
    $('#visual').scroll(jsPlumb.repaintEverything);

    var stack_variables = all_variables.stacks || [];
    var unique_id_list = filter_stacks(stack_variables)[0];
    if (unique_id_list !== undefined) {
        $.ajax({
            type: "POST",
            url: "getHeapObjects",
            data: JSON.stringify({
                heap_requests: unique_id_list
            }),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {
                var heap = d3.select('#heap').append("div").attr("id", "heapBody");
                all_objects = data.objects;
                append_heap(all_objects);
                draw_arrows();
            }
        });
    }

    var stack_td = d3.select("#stack");

    var stackFrames = stack_td.append("div")
        .attr("id", "stackFrames");

    append_stacks(stackFrames, stack_variables);
}

function append_stacks(stack_selection, stack_variables) {

    // create a div for each stack
    var stack = stack_selection.selectAll("div")
        .data(stack_variables)
        .enter()
        .append("div");
    stack.attr("class", "stackFrame")
        .attr("id", function(d, i) {
            return "stack" + i;
        });

    var stack_panel = stack.append("div")
        .attr("class", "panel panel-default");
        
    // name of the stack
    var stackHeaders = stack_panel.append("div")
        .attr("class", "panel-heading")
        .attr("id", function(d, i) {
            return "stackHeader" + i;
        })
        .text(function(d) {
            return d.methodName;
        });

    // variable table
    var stackTables = stack_panel.append("table")
        .attr("class", "stackFrameVarTable")
        .attr("id", function(d, i) {
            return "stackVarTable" + i;
        });

    var stackVariables = stackTables.selectAll("tr")
        .data(function(d, i) {
            return d.stack;
        })
        .enter()
            .append("tr");

    stackVariables.attr("class", "variableTr")
        .attr("id", function(d, i) {
            return "stack_" + d.name + "_tr";
        });

    stackVariables.append("td")
        .attr("class", "stackFrameVar")
        .text(function(d) {
            return d.name;
        });

    var stackValues = stackVariables.append("td")
        .attr("class", "stackFrameValue")
        .attr("id", function(d) {
            if (d.unique_id)
                return "stackFrameValue_heap_" + d.unique_id;
            else
                return "stackFrameValue_" + d.name;
        });

    //jsPlumb.addEndpoint(".pointer", { endpoint: "Dot"});
    populate_values(stackValues);
}

function draw_arrows() {
    var objects = d3.select("#heapBody").selectAll("div");
    console.log("in draw_arrows", objects);
    var pointers = d3.selectAll(".pointer");

    pointers.each(function(p) {
       jsPlumb.addEndpoint(this, {
        endpoint :["Dot", {radius: 5, cssClass: "stackPoint" }],
        anchor: [0.5, 0.5, 1, 1]

       });
    });

    objects.each(function(d) {
        var obj = this;
        console.debug("object thing", d);
        var pointers = d3.selectAll(".pointer_to_" + obj.id);
        pointers.each(function(p) {
            jsPlumb.connect({
                source: this,
                target: obj,
                cssClass: "connectLine"
            });
        });
    });
}

function is_primative(d) {
    return !is_null(d) && primitive_list.indexOf(d.type) >= 0;
}

function is_null(d) {
    return d === undefined || d.value == "null";
}

function is_object(d) {
    return !is_null(d) && !is_primative(d) && d.unique_id !== undefined;
}

function toggle_object_visibility(id, type) {
    if (object_visible(id)) {
        var index = visible_objects.indexOf(id);
        visible_objects.splice(index, 1);
    } else {
        visible_objects.push(id);
    }
    ensure_object(id, type);
    append_heap(all_objects);
    jsPlumb.deleteEveryEndpoint();
    draw_arrows();
}

function ensure_object(id, type) {
    var exists = all_objects.some(function(o) {
        return id == o.unique_id;
    });
    if (!exists) {
        load_object(id, type);
    }
}

function load_object(id, type) {
    $.ajax({
        type: "POST",
        url: "getHeapObjects",
        data: JSON.stringify({
            heap_requests: [{unique_id: id, type: type}]
        }),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(data) {
            if (data.objects === undefined) {
                return;
            }
            console.log(data);
            all_objects = all_objects.concat(data.objects);
            append_heap(all_objects);
            draw_arrows();
        }
    });
}

function object_visible(id) {
    return visible_objects.indexOf(id) != -1;
}

/* Populates the stack with the values
 * (the actual value for primitive types and with arrows for objects). */
function populate_values(selection) {
    var primitives = selection.filter(is_primative);
    var objects = selection.filter(is_object);
    var null_objects = selection.filter(is_null);

    // null objects get special null value
    null_objects.append("span")
        .attr("class", "primitives")
        .text(function(d) {
            return '⊥';
        });
    console.log("object", objects);
    objects
        .on("click", function(d) {
            toggle_object_visibility(d.unique_id, d.type);
        })
        .append("span")
        .html("&nbsp;")
        .attr("class", function(d) {
            return 'pointer' + ' ' + 'pointer_to_object_' + d.unique_id;
        })
        .attr("title", "click to expand");


    // primitive values are drawn in the cell
    primitives.append("span")
        .attr("class", "primitives")
        .text(function(d) {
            return d.value;
        });
}

// Creates the heap and the objects in it.
function append_heap(heap_objects) {
    var visible_objects = heap_objects
        .filter(function(d) {
            return object_visible(d.unique_id);
        });
    visible_objects.sort(function (a, b) { return a.unique_id - b.unique_id; });

    var heap = d3.select("#heapBody");

    // create the actual objects
    var the_objects = heap.selectAll("div .heapRow")
        .data(visible_objects, function(d) { return d.unique_id; });

    the_objects.exit().remove();

    var objects = the_objects.enter()
        .append("div")
            .attr("id", function(d, i) {
                return "object_" + d.unique_id;
            })
            .attr("class", "heapRow");

    // label each object according to type
    /*
    objects.append("div")
        .attr("class", "typeLabel")
        .text(function(d, i) {
            if (is_of_type(d, type_object)) {
                return get_class_name(d.type);
            }
            return d.object_type;
        });
     */

    // we have three diffrent types of things, arrays, strings and 'real'
    // objects
    var strings = objects.filter(function(d) {
        return is_of_type(d, type_string);
    });

    var arrays = objects.filter(function(d) {
        return is_of_type(d, type_array);
    });

    var pure_objects = objects.filter(function(d) {
        return is_of_type(d, type_object);
    });

    var array_panels = arrays.append("div")
        .attr("class", "panel panel-success");
        
        
    var array_panel_heading = array_panels.append("div")
        .attr("class", "panel-heading")
        .text("ARRAY");
        
    var array_tables = array_panels.append("table")
        .attr("class", function(d, i) {
            return d.object_type;
        });

    var array_values = array_tables
        .append("tr")
        .attr("class", "value")
        .selectAll("td")
        .data(function(d) { return d.array; })
        .enter().append("td");

    var array_indexes = array_tables
        .append("tr")
        .attr("class", "indice")
        .selectAll("td")
        .data(function(d) { return d.array; })
        .enter().append("td")
            .text(function(d, i) { return i; });

    populate_values(array_values);

    var string_panels = strings.append("div")
        .attr("class", "panel panel-warning");
              
    var string_panel_heading = string_panels.append("div")
        .attr("class", "panel-heading")
        .text("STRING");
        
    var string_tables = string_panels.append("table")
        .attr("class", function(d, i) {
            return d.object_type;
        });

    var string_values = string_tables
        .append("tr")
        .attr("class", "value")
        .selectAll("td")
        .data(function(d) { return d.string; })
        .enter().append("td")
            .text(function(d, i) { return d; });

    var string_indexes = string_tables
        .append("tr")
        .attr("class", "indice")
        .selectAll("td")
        .data(function(d) { return d.string; })
        .enter().append("td")
            .text(function(d, i) { return i; });
    
    var object_panels = pure_objects.append("div")
        .attr("class", "panel panel-info");
              
    var object_panel_heading = object_panels.append("div")
        .attr("class", "panel-heading")
        .text(function(d, i) {
            if (is_of_type(d, type_object)) {
                return get_class_name(d.type);
            }
            return d.object_type;
        });
        
    var pure_object_tables = object_panels.append("table")
        .attr("class", function(d, i) {
            return d.object_type;
        });

    var pure_object_rows = pure_object_tables.selectAll("tr")
        .data(function(d) { return d.fields; })
        .enter()
            .append("tr");

    var pure_objects_vars = pure_object_rows
        .append("td")
        .text(function(d, i) {
            return d.name;
        });

    var pure_object_values = pure_object_rows
        .append("td")
        .datum(function(d) {
            return d.value;
        });
    populate_values(pure_object_values);

}


//function create_arrows(selection, unique_id_list) {
//    // makes connectors undraggable
//    jsPlumb.importDefaults({
//        ConnectionsDetachable: false,
//        position: "relative"
//    });
//
//    jsPlumb.bind("ready", function() {
//        jsPlumb.Defaults.Container = "visual";
//        var n = unique_id_list.length;
//        for (var i = 0; i < n; i++) {
//            var source = jsPlumb.addEndpoint("stackFrameValue_heap_" + unique_id_list[i].unique_id, {
//                anchor: [0.5, 0.5, 1, 1],
//                endpoint: ["Dot", {
//                    radius: 5
//                }],
//                connectionsDetachable: false,
//                cssClass: "stackPoint"
//            });
//
//            var target = jsPlumb.addEndpoint("heap_object_" + unique_id_list[i].unique_id, {
//                anchor: "Left",
//                endpoint: "Blank",
//                connectionsDetachable: false
//            });
//            jsPlumb.connect({
//                source: source,
//                target: target,
//                overlays: [
//                    ["Arrow", {
//                        width: 6,
//                        length: 6,
//                        location: 1
//                    }]
//                ],
//                Connector: ["State Machine", {
//                    proximityLimit: 10
//                }],
//                cssClass: "connectLine"
//            });
//        }
//    });
//}
//
//function addEndPointsToArrayElems(array_elems_uid) {
//    console.log("array_uid", array_elems_uid);
//    var n = array_elems_uid.length;
//    for (var i = 0; i < n; i++) {
//        console.log("array_" + array_elems_uid[i]);
//        jsPlumb.ready(function() {
//            jsPlumb.addEndpoint("array_" + array_elems_uid[i], {
//                cssClass: "stackPoint",
//                endpoint: ["Dot", {
//                    radius: 5
//                }],
//                anchor: [0.5, 0.5, 1, 1],
//                connectionsDetachable: false
//            });
//        });
//
//    }
//}

/* Utility functions */


function filter_stacks(stack_variables) {
    var map = Array.prototype.map;

    var filtered_stacks = map.call(stack_variables, filter_one_stack);
    var one_filtered_stack = [];
    var n = filtered_stacks.length;
    for (var i = 0; i < n; i++) {
        one_filtered_stack.push(filtered_stacks[i]);
    }
    return one_filtered_stack;
}

function filter_one_stack(one_stack) {
    var map = Array.prototype.map;
    var stack = one_stack.stack;

    stack = stack.filter(function(d) {
        return d.unique_id !== undefined;
    });
    stack = map.call(stack, function(d) {
        return {
            unique_id: d.unique_id,
            type: d.type
        };
    });
    return stack;
}

// Returns a set of all the objects of type 'type'.
function filter_heap(heap_objects, type) {
    heap_objects.filter(function(d) {
        return d.object_type == type;
    });
}

// Returns true if the object is of type 'type', false otherwise.
function is_of_type(heap_element, type) {
    return heap_element.object_type == type;
}

function is_empty_object(obj) {
    return obj == empty_object;
}

function get_class_name(type) {
    var s = type.split(".");
    var actual_type = s[s.length - 1];
    return actual_type;
}
