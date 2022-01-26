var App = React.createClass({
  getInitialState: function() {
    return { items: [], files: [],versions: [],content:{} ,uploadresponse:{},contentvalue:'',filestatus:''};
  },

  componentDidMount: function() {
    fetch("/tasksvn/praveen/fetchall",{
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
}).then(function(response) {
        return response.json();
    }).then(this.getListOfFiles);
    
  },
  
  getVersions: function(response) {
	this.setState({
	  versions: response})
	  this.setState({filestatus:''});
  },
  
  fetchListOfFiles: function() {
	    fetch("/tasksvn/praveen/fetchall",{
	        method: 'GET',
	        headers: { 'Content-Type': 'application/json' },
	}).then(function(response) {
	        return response.json();
	    }).then(this.getListOfFiles);  
  },
  getListOfFiles: function(response) {
	  var obj = {};
	  for ( var i=0, len=response.length; i < len; i++ )
	      obj[response[i]['filename']] = response[i];

	  response = new Array();
	  for ( var key in obj )
		  response.push(obj[key]);
	  
	this.setState({
	  files: response
	});
	this.setState({uploadresponse:{}});
  },
  getContent: function(response) {
	this.setState({
	  content: response
    });
	this.setState({contentvalue:response.content})
  },
  change: function(event){
      this.setState({filename: event.target.value});
      fetch("/tasksvn/praveen/fetchfilebyname/"+event.target.value,{
          method: 'GET',
          headers: { 'Content-Type': 'application/json' },
  }).then(function(response) {
          return response.json();
      }).then(this.getVersions);
  },
  onFileUpload: function(){
	  const formData = new FormData();

	  formData.append('file', this.state.selectedFile);
      fetch("/tasksvn/praveen/uploadfile/",{
          method: 'POST',
          body: formData,
  }).then(function(response) {
          return response.json();
      }).then(this.uploadResponse);
  },
  uploadResponse: function(response){
	 this.setState({ uploadresponse: response }); 
	 this.fetchListOfFiles()
  },
  changeVersion: function(event){
      this.setState({version: event.target.value});
      fetch("/tasksvn/praveen/readfile/"+event.target.value+"/"+this.state.filename,{
          method: 'GET',
          headers: { 'Content-Type': 'application/json' },
  }).then(function(response) {
          return response.json();
      }).then(this.getContent);
  },
  submit: function(event){
      this.setState({version: event.target.value});
      var resp={
        version: this.state.version,
        content: this.state.contentvalue,
    	filename: this.state.filename
      }
      fetch("/tasksvn/praveen/editfile/",{
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(resp)
  }).then(function(response) {
          return response.json();
      }).then(this.submittedFile);
  },
  submittedFile: function(event){
	  this.setState({ items: []});
	  this.setState({files: []});
	  this.setState({versions: []});
	  this.setState({content:{}});
	  this.setState({filestatus:'file edited successfully'});
  },
  onFileChange: function(event){
	 this.setState({ selectedFile: event.target.files[0] }); 
  },
  handleChange: function(event) {
	 this.setState({contentvalue: event.target.value});
  },

  render: function() {
    return (
      <div className="container">
      <div className=" col-md-4">
      <select id="files" onChange={this.change} value={this.state.filename}>
      <option>select file</option>
      {this.state.files.map((record,index) => <option key={index} value={record.filename} >{record.filename}</option>)}
      </select>
      </div>
      <div className=" col-md-4">
      {this.state.filestatus}
      </div>
        <div className=" col-md-4">
        <select id="versions" onChange={this.changeVersion} value={this.state.version}>
        <option>select version</option>
        {this.state.versions.map((versions,index) => <option key={index} value={versions.version} >{versions.version}</option>)}
        </select>
        		
       </div>
       <div className=" col-md-12">
       <textarea className="textareaclass" value={this.state.contentvalue} onChange={this.handleChange}></textarea>
       </div>
       <div className=" col-md-6">
       <input type="file" onChange={this.onFileChange} /> 
       <button onClick={this.onFileUpload}> 
         Upload! 
       </button> 
         <div>
         {this.state.uploadresponse.status}
         </div>
       </div>
       <div className=" col-md-6">
       <button className="button" onClick={this.submit} >Submit</button>
       </div>
    </div>
    );
  }
});

ReactDOM.render(<App />, document.getElementById("app"));