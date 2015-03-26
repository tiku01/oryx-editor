# Introduction #

The Poem-Backend provides functionality for quering the model uri with restrictions.
There are dedicated serch terms with limited values range to determine the response set of models. The Model Repository provides links to a part of possible filter combinations. Basically all search terms can be mixed.

# Details #

### type ###
The type-filter restricts the response-set to one type of models. There is no possibility to query two or more types. If the type is undefined, the result set contains models of any type.

Possible values: bpmn, epc, workflownet, petrinet


### owner ###
This filter determines if the result set only contains models created by the quering user or not. By default this filter is false.

Possible values: true
All other values trigger the default behavior

### is\_shared ###
This filter is only relevant if must\_be\_owner is true. The result set contains only models that have at least one reader or contributor. By default this filter is false.

Possible values: true
All other values trigger the default behavior

### from and to ###
With the keywords from and to it is possible to define a specific range of time. Default is from 01-01-1970 to now. If the last update of a model is within this range, it is contained in the result set. Of course only models the user is allowed to view.

Possible values: Dates in format dd-mm-yyyy

### is\_public ###
The result set contains only models that are public visible. By default this filter is false.

Possible values: true
All other values trigger the default behavior

### contributor ###
The result set contains only models the user contributes to. By default this filter is false.

Possible values: true
All other values trigger the default behavior

### reader ###
The result set contains only (unpublished) models the user can read but not write. By default this filter is false.

Possible values: true
All other values trigger the default behavior