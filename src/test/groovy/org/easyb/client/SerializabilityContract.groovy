import org.easyb.domain.Specification
import org.easyb.domain.GroovyShellConfiguration
import org.easyb.domain.Story

narrative "external client contacts", {
  as_a "application which uses easyb"
  i_want "easyb behaviors to be serializable"
  so_that "I can store them between test runs"
}

scenario "specifications", {
  given "a specification", {
    spec = new Specification(new GroovyShellConfiguration(), "phase", new File(""))
  }
  when "that behavior is serialized", {
    new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(spec)
  }
  then "serialization occurs without error"
}

scenario "stories", {
  given "a story", {
    story = new Story(new GroovyShellConfiguration(), "phase", new File(""))
  }
  when "that behavior is serialized", {
    new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(story)
  }
  then "serialization occurs without error"
}