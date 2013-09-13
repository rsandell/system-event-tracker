import net.joinedminds.tools.evet.Functions

String rootUrl = Functions.rootUrl

def st = namespace("jelly:stapler")

script(src: "${rootUrl}/js/timeline-view.js", type: "text/javascript")
st.adjunct(includes: "org.kohsuke.stapler.simile.timeline")