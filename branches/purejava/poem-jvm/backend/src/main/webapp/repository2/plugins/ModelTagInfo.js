
Ext.namespace('Repository.Plugins');

Repository.Plugins.ModelTagInfo = function(facade) {
	this.name = 'Model Tags';
	this.facade = facade;
	this.panel = this.facade.registerPluginOnPanel(this.name, 'right');

	// Textbox
	this.field = new Ext.form.Field({id:'model_tags_add_tag'});
	this.panel.add(this.field);
	// Button
	this.testButton = new Ext.Button({ text : 'Tag Test' });
	this.testButton.addListener('click', function() {
		if (this.field.getValue().length > 0) {
			this.field.getValue().split(',').each(function(tag){
				this.facade.getSelectedModels.each(function(modelId){
					uri = this.facade.modelCache.getModelUri().substring(1); // Remove leading slash
					Ext.Ajax.request({
						url : uri + 'tag', 
						method : 'post', 
						params : {tag_name : tag}, 
						success : function(response) {
							data = Ext.util.JSON.decode(response.responseText);
							data.each(function (pair) {
								this.facade.modelCache.updateObject('/tags', pair.key, pair.value);
							}.bind(this))
						}.bind(this)})
				}.bind(this))
			}.bind(this));
		} 
	}.bind(this));
	this.panel.add(this.testButton);
	this.panel.doLayout();
	this.facade.registerOnSelectionChanged(this.selectionChanged.bind(this));
}

Repository.Plugins.ModelTagInfo.prototype = {
	render : function(modelData) {
		if (this.assignedTagsPanel) {
			this.panel.remove(this.assignedTagsPanel);
		}
		this.assignedTagsPanel = new Ext.Panel({id: 'model_tags_assigned_tags_panel'});
		// commonTags is the intersection of all tags of all selected models
		var commonTags = modelData.get(modelData.keys()[0]);
		modelData.each(function(pair) {
			var modelTags = modelData.get(pair.value);
			commonTags.each(function(tag) {
				if (modeltags.indexOf(tag) == -1) {
					commonTags = commonTags.without(tag);
				}
			}.bind(this));
			
		}.bind(this));
		commonTags.value.each(function(tag) {
			var button = new Ext.button({text:tag});
			assignedTagsPanel.add(button);
		}.bind(this));
		this.panel.add(assignedTagsPanel);
		this.panel.doLayout();
	},
	
	selectionChanged : function(modelIds) {
		this.facade.modelCache.getDataAsync('/tags', modelIds, this.render.bind(this));
	}
}