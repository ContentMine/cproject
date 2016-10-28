package org.xmlcml.cproject.files;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cproject.CMineFixtures;
import org.xmlcml.cproject.args.DefaultArgProcessor;
import org.xmlcml.cproject.metadata.AbstractMetadata;
import org.xmlcml.cproject.util.CMineTestFixtures;
import org.xmlcml.cproject.util.Utils;
import org.xmlcml.html.HtmlElement;

import com.google.common.collect.Multimap;

import nu.xom.Element;

public class CProjectTest {

	
	private static final Logger LOG = Logger.getLogger(CProjectTest.class);
	private static File ZIKA_DIR = new File(CMineFixtures.TEST_RESULTS_DIR, "zika");

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCProject() {
		File project1Dir = new File(CMineFixtures.TEST_PROJECTS_DIR, "project1");
		Assert.assertTrue(project1Dir.exists());
		CContainer cProject = new CProject(project1Dir);
		File projectDir = cProject.getDirectory();
		Assert.assertTrue(projectDir.exists());
	}
	
	@Test
	public void testCProjectManifest() {
		CContainer cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project1"));
		CProjectManifest manifest = (CProjectManifest) cProject.getOrCreateManifest();
		Assert.assertNotNull(manifest);
		File manifestFile = manifest.getOrCreateManifestFile();
		Element manifestElement = manifest.getOrCreateManifestElement();
		Assert.assertNotNull("need manifest file in "+cProject.getDirectory(), manifestFile);
	}

	@Test
	public void testManifestDocs() {		
		CContainer cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project1"));
		CProjectManifest manifest = (CProjectManifest) cProject.getOrCreateManifest();
		HtmlElement docElement = manifest.getDocHtml();
		LOG.trace(docElement);
	}

	@Test
	public void testUpdateManifest() {
		CContainer cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project1"));
		cProject.updateManifest();
	}
	
	@Test
	public void testGetCTreeList() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project1"));
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 2, allChildDirectoryList.size());
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 1, allChildFileList.size());
		CTreeList cTreeList = cProject.getResetCTreeList();
		Assert.assertEquals("trees", 2, cTreeList.size());
	}
	
	@Test
	public void testGetCTreeListWithPrefixes() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_OPEN_DIR, "lic20160201"));
		CTreeList cTreeList = cProject.getResetCTreeList();
		Assert.assertEquals("ctrees", 123, cTreeList.size());
		List<String> doiPrefixes = cProject.getDOIPrefixList();
		Assert.assertEquals("prefixes", 123, doiPrefixes.size());
		CTreeList list1063 = cProject.getCTreesWithDOIPrefix("10.1063");
		Assert.assertEquals("prefixed", 1, list1063.size());
		CTreeList list1088 = cProject.getCTreesWithDOIPrefix("10.1088");
		Assert.assertEquals("prefixed", 74, list1088.size());
	}
	
	@Test
	public void testGetCTreeListsByPrefixes() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_OPEN_DIR, "lic20160201"));
		Multimap<String, CTree> cTreesByPrefix = cProject.getCTreeListsByPrefix();
		Assert.assertEquals(5, cTreesByPrefix.keySet().size());
		List<CTreeList> cTreeLists = CTreeList.getCTreeListsSortedByCount(cTreesByPrefix);
		Assert.assertEquals(5, cTreeLists.size());
		Assert.assertEquals("first ", 74, cTreeLists.get(0).size());
		Assert.assertEquals("last ", 1, cTreeLists.get(cTreeLists.size() - 1).size());
	}
	
	@Test
	/** this is a small set downloaded from EPMC
	 * 
	 */
	public void testCTreeContent2() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2"));
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 3, allChildDirectoryList.size());
		List<File> allowedChildDirectoryList = cProject.getAllowedChildDirectoryList();
		// this is zero because the CTrees are not counted as allowedDirectories
		Assert.assertEquals("allowed child dir", 0, allowedChildDirectoryList.size());
		List<File> unknownChildDirectoryList = cProject.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 0, unknownChildDirectoryList.size());
		CTreeList cTreeList = cProject.getResetCTreeList();
		Assert.assertEquals("all child dir", 3, cTreeList.size());
		
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 2, allChildFileList.size());
		List<File> allowedChildFileList = cProject.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		List<File> unknownChildFileList = cProject.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 0, unknownChildFileList.size());
		
		CTree cTree1 = cTreeList.get(0);
		cTree1.getOrCreateFilesDirectoryCTreeLists();

	}

	@Test
	/** this is an irregular structure to test the system.
	 * 
	 */
	@Ignore // fails with some people
	public void testCTreeContent3() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project3"));
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 3, allChildDirectoryList.size());
		List<File> allowedChildDirectoryList = cProject.getAllowedChildDirectoryList();
		// this is zero because the CTrees are not counted as allowedDirectories
		Assert.assertEquals("allowed child dir", 0, allowedChildDirectoryList.size());
		List<File> unknownChildDirectoryList = cProject.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 1, unknownChildDirectoryList.size());
		CTreeList cTreeList = cProject.getResetCTreeList();
		Assert.assertEquals("all child dir", 2, cTreeList.size());
		
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 3, allChildFileList.size());
		List<File> allowedChildFileList = cProject.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		List<File> unknownChildFileList = cProject.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 1, unknownChildFileList.size());
		
		// cTree1 is a normal CTree
		CTree cTree1 = cTreeList.get(0);
		allowedChildDirectoryList = cTree1.getAllowedChildDirectoryList();
		Assert.assertEquals("allowed child dir", 1, allowedChildDirectoryList.size());
		unknownChildDirectoryList = cTree1.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 0, unknownChildDirectoryList.size());
		
		allChildFileList = cTree1.getAllChildFileList();
		Assert.assertEquals("all child file", 2, allChildFileList.size());
		allowedChildFileList = cTree1.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		unknownChildFileList = cTree1.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 0, unknownChildFileList.size());

		// cTree2 is a normal CTree, without results/
		CTree cTree2 = cTreeList.get(0);
		allowedChildDirectoryList = cTree2.getAllowedChildDirectoryList();
		Assert.assertEquals("allowed child dir", 1, allowedChildDirectoryList.size());
		unknownChildDirectoryList = cTree2.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 0, unknownChildDirectoryList.size());
		
		allChildFileList = cTree2.getAllChildFileList();
		Assert.assertEquals("all child file", 2, allChildFileList.size());
		allowedChildFileList = cTree2.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		unknownChildFileList = cTree2.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 0, unknownChildFileList.size());

		// there are only 2 CTrees as the nonctree/ has no reserved filenames
		
	}
	
	@Test
	@Ignore // sorting problem
	public void testRelativeProjectPath() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project3"));
		String relativePath = cProject.getRelativeProjectPath(cProject.getResultsXMLFileList().get(0));
		Assert.assertEquals("relpath", "ctree1/results/sequence/dnaprimer/results.xml", relativePath);
	}

	@Test
	public void testResultsXML() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project3"));
		List<File> resultsXMLFileList = cProject.getResultsXMLFileList();
		Assert.assertEquals("all results.xml", 2, resultsXMLFileList.size());
		cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "regex10"));
		List<File> resultsXMLFiles = cProject.getResultsXMLFileList();
		Assert.assertEquals("all results.xml", 10, resultsXMLFiles.size());
		Assert.assertEquals("all results.xml", 9, cProject.getResultsXMLFileList(CProject.OMIT_EMPTY).size());
//		List<String> relativePaths = cProject.getRelativeProjectPaths(resultsXMLFiles);
		/** possible unpredictable order
		Assert.assertEquals("relative paths", 
				"["
				+ "e0115544/results/regex/consort0/results.xml,"
				+ " e0116215/results/regex/consort0/results.xml,"
				+ " e0116596/results/regex/consort0/results.xml,"
				+ " e0116903/results/regex/consort0/results.xml,"
				+ " e0117956/results/regex/consort0/results.xml,"
				+ " e0118659/results/regex/consort0/results.xml,"
				+ " e0118685/results/regex/consort0/results.xml,"
				+ " e0118692/results/regex/consort0/results.xml,"
				+ " e0118792/results/regex/consort0/results.xml,"
				+ " e0119090/results/regex/consort0/results.xml"
				+ "]",
				relativePaths.toString());
				*/
	}
	
	@Test
	public void testUnzip() {
		
	}
	
	
	@Test
	@Ignore
	public void testGlobFileListHuge() {
		File patentFile = new File("../patents");
		if (!patentFile.exists()) return; // only for PMR
		CProject cProject = new CProject(new File(patentFile, "US08979"));
		ProjectFilesTree projectFilesTree = cProject.extractProjectFilesTree("**/*");
		Assert.assertEquals(995,  projectFilesTree.size());
		Assert.assertEquals(13,  projectFilesTree.get(0).size());
	}

	
	@Test
	@Ignore
	public void testGlobFileListMedium() throws IOException {
		File targetDir = new File("target/patents/US08979");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_MISC_DIR, "patents/US08979"), targetDir);
		String args = "-i scholarly.html --project "+targetDir;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		CProject cProject = argProcessor.getCProject();
		ProjectFilesTree treeFilesList = cProject.extractProjectFilesTree("**/*");
		Assert.assertEquals(71,  treeFilesList.size());
		CTreeFiles treeFiles0 = treeFilesList.get(0);
		Assert.assertTrue(treeFiles0.size() > 11 && treeFiles0.size() < 14);
//		Assert.assertEquals(13,  treeFiles0.size());
		treeFiles0.sort();
		/** sort instability
		Assert.assertEquals("treeFiles",  ""
				+"<cTreeFiles cTree=\"target/patents/US08979/US08979000-20150317\">"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/fulltext.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/gene/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/consort0/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/plasmid/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbioPhrases/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/binomial/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/genus/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.html\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/scholarly.html\" />"
				+ "</cTreeFiles>",				
				treeFiles0.toString()
				);
				*/
	}
	
	
	@Test
	@Ignore
	public void testGlobFileListMedium1() throws IOException {
		File targetDir = new File("target/patents/US08979");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_MISC_DIR, "patents/US08979"), targetDir);
		String args = "--filter file(**/fulltext.xml)xpath(//description[heading[.='BACKGROUND']]/p[contains(.,'polymer')]) --project "+targetDir+" -o background.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}


	@Test
	public void testGlobFileListSmallCommand() throws IOException {
		File targetDir = new File("target/patents/US08979small");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_MISC_DIR, "patents/US08979small"), targetDir);
		String args = "-i scholarly.html --filter file(**/*) --project "+targetDir+" --output cTreeList.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		CProject cProject = argProcessor.getCProject();
		ProjectFilesTree treeFilesList = cProject.extractProjectFilesTree("**/{empty,results,fulltext,scholarly}.{xml,html}");
		
		Assert.assertEquals(5,  treeFilesList.size());
		CTreeFiles treeFiles0 = treeFilesList.get(0);
		Assert.assertTrue("xmlfiles", treeFiles0.size() >= 13 && treeFiles0.size() <= 14);
		treeFiles0.sort();
		/**
		Assert.assertEquals("treeFiles",  
				"<cTreeFiles cTree=\"target/patents/US08979small/US08979000-20150317\">"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/fulltext.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/gene/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/regex/consort0/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/regex/plasmid/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/regex/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/search/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/search/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/search/synbioPhrases/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/species/binomial/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/species/genus/empty.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/word/frequencies/results.html\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/results/word/frequencies/results.xml\" />"
				+ "<file name=\"target/patents/US08979small/US08979000-20150317/scholarly.html\" /></cTreeFiles>",
				treeFiles0.toString()
				);
				*/
	}

	@Test
	public void testGlobFileXPathSmallCommand() throws IOException {
		File targetDir = new File("target/patents/US08979small");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_MISC_DIR, "patents/US08979small"), targetDir);
		String args = "--filter file(**/fulltext.xml)xpath(//country) --project "+targetDir+" --output country.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		File outputFile = new File(targetDir, "country.xml");
		Assert.assertTrue("output file", outputFile.exists());
		CProject cProject = argProcessor.getCProject();
		ProjectFilesTree projectFilesTree = cProject.getProjectFilesTree();
		Assert.assertEquals("trees",  5, projectFilesTree.size());
		CTreeFiles treeFiles0 = projectFilesTree.get(0);
//		LOG.debug(treeFiles0);
		/**
		Assert.assertEquals("treefiles0",  
		"<cTreeFiles cTree=\"target/patents/US08979small/US08979000-20150317\"><file name=\"target/patents/US08979small/US08979000-20150317/fulltext.xml\" /></cTreeFiles>",
		treeFiles0.toString()
		);
		*/

	}

	@Test
	@Ignore
	public void testGlobFileListMediumCommand() throws IOException {
		File targetDir = new File("target/patents/US08979");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_MISC_DIR, "patents/US08979"), targetDir);
		String args = "--filter file(**/*) --project "+targetDir;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		CProject cProject = argProcessor.getCProject();
		ProjectFilesTree treeFilesList = cProject.extractProjectFilesTree("**/*");
		Assert.assertEquals(71,  treeFilesList.size());
		CTreeFiles treeFiles0 = treeFilesList.get(0);
//		Assert.assertEquals(13,  treeFiles0.size());
		Assert.assertTrue(treeFiles0.size() > 11 && treeFiles0.size() < 14);
		treeFiles0.sort();
		/** unstable wrt sort
		Assert.assertEquals("treeFiles",  ""
				+"<cTreeFiles cTree=\"target/patents/US08979/US08979000-20150317\">"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/fulltext.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/gene/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/consort0/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/plasmid/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbioPhrases/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/binomial/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/genus/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.html\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/scholarly.html\" />"
				+ "</cTreeFiles>",				
				treeFiles0.toString()
				);
				*/
	}

	
	@Test
	@Ignore
	public void testGlobFileListHugeResults() {
		File patentFile = new File("../patents");
		if (!patentFile.exists()) return; // only for PMR
		CProject cProject = new CProject(new File(patentFile, "US08979"));
		ProjectFilesTree fileListList = cProject.extractProjectFilesTree("**/results.xml");
		Assert.assertEquals(995,  fileListList.size());
		Assert.assertEquals(1,  fileListList.get(0).size());
		Assert.assertEquals("../patents/US08979/US08979000-20150317/results/word/frequencies/results.xml",  
				fileListList.get(0).get(0).toString());
	}

	/** files for project2 are:
	 * /Users/pm286/workspace/cproject/src/test/resources/org/xmlcml/files/projects
	 * 
$ tree project2
project2
├── PMC4417228
│   ├── fulltext.pdf
│   └── fulltext.xml
├── PMC4521097
│   ├── fulltext.pdf
│   └── fulltext.xml
├── PMC4632522
│   ├── fulltext.pdf
│   └── fulltext.xml
├── eupmc_results.json
└── log.xml

	 * 
	 */
	@Test
	public void testGlobFileList() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2"));
		ProjectFilesTree projectFilesTree = cProject.extractProjectFilesTree("**/*");
		// 3 CTrees of form PMCddddddd
		Assert.assertEquals("a", 3,  projectFilesTree.size());
		// the first one has two child files (fulltext.pdf and fulltext.xml)
		CTreeFiles cTreeFiles = projectFilesTree.get(0);
		Assert.assertNotNull("files", cTreeFiles);
		Assert.assertEquals("b", 2,  cTreeFiles.size());
		// fails on unsorted lists
		
//		Assert.assertEquals("c", "src/test/resources/org/xmlcml/files/projects/project2/PMC4417228/fulltext.pdf",  
//				fileListList.get(0).get(0).toString());
//		Assert.assertEquals("d", "src/test/resources/org/xmlcml/files/projects/project2/PMC4417228/fulltext.xml",  
//				fileListList.get(0).get(1).toString());
	}

	@Test
	@Ignore
	public void testGlobFileListAndXPathSearch() throws IOException {
		File targetDir = new File("target/glob/project2/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/"), targetDir);
		CProject cProject = new CProject(targetDir);
		ProjectSnippetsTree projectSnippetsTree = cProject.extractProjectSnippetsTree("**/fulltext.xml", "//title[starts-with(.,'Data')]");
		/**
<projectSnippetsTree>
  <snippetsTree>
    <snippets file="target/glob/project2/ctree1/PMC4417228/fulltext.xml">
	  <title>Data collection</title>
	  <title>Data analysis</title>
	</snippets>
  </snippetsTree>
  <snippetsTree>
    <snippets file="target/glob/project2/ctree1/PMC4632522/fulltext.xml">
	  <title>Data accessibility</title>
	</snippets>
  </snippetsTree>
</projectSnippetsTree>
*/		
		Assert.assertEquals("snippetsTrees", 2, projectSnippetsTree.size());
		// needs sorted output
//		SnippetsTree snippetsTree0 = projectSnippetsTree.get(0);
//		Assert.assertEquals("snippet", 1, snippetsTree0.size());
//		XMLSnippets snippets0 = snippetsTree0.get(0);
		
//		Assert.assertEquals("snippets0", ""
//				+ "<snippets file=\"target/glob/project2/ctree1/PMC4417228/fulltext.xml\">"
//				+ "<title>Data collection</title><title>Data analysis</title>"
//				+ "</snippets>",
//				snippets0.toXML());
	}
	

	@Test
	/**
	 * SHOWCASE
	 * 
	 */
	public void testGlobFileListAndXPathSearchCommand() throws IOException {
		File targetDir = new File("target/glob/project2/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/"), targetDir);
		String output = "snippets.xml";
		String args = " --project " + targetDir+" --filter file(**/fulltext.xml)xpath(//title[starts-with(.,'Data')]) -o "+output;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		
		ProjectSnippetsTree projectSnippetsTree = argProcessor.getProjectSnippetsTree();
		Assert.assertNotNull("projectSnippetsTree not null", projectSnippetsTree);
		Assert.assertEquals("snippetsTrees", 2, projectSnippetsTree.size());
		/**
		Assert.assertEquals("snippets",
		"<projectSnippetsTree>"
		+ "<snippetsTree>"
		+   "<snippets file=\"target/glob/project2/ctree1/PMC4417228/fulltext.xml\">"
		+     "<title>Data collection</title>"
		+     "<title>Data analysis</title>"
		+   "</snippets>"
		+  "</snippetsTree>"
		+  "<snippetsTree>"
		+    "<snippets file=\"target/glob/project2/ctree1/PMC4632522/fulltext.xml\">"
		+      "<title>Data accessibility</title>"
		+    "</snippets>"
		+  "</snippetsTree>"
		+ "</projectSnippetsTree>",
		projectSnippetsTree.toString()
		);
		*/

		// this is not wriiten, because projectsFile takes precedence
		ProjectFilesTree projectFilesTree = argProcessor.getProjectFilesTree();
		Assert.assertEquals("filesTrees", 2, projectFilesTree.size());
		/**
		Assert.assertEquals("files",
			"<cTreeFilesTree project=\"target/glob/project2/ctree1\">"
			+ "<cTreeFiles cTree=\"target/glob/project2/ctree1/PMC4417228\">"
			+   "<file name=\"target/glob/project2/ctree1/PMC4417228/fulltext.xml\" />"
			+  "</cTreeFiles>"
			+  "<cTreeFiles cTree=\"target/glob/project2/ctree1/PMC4632522\">"
			+    "<file name=\"target/glob/project2/ctree1/PMC4632522/fulltext.xml\" />"
			+  "</cTreeFiles>"
			+ "</cTreeFilesTree>",
			projectFilesTree.toString()
			);
			*/
	}
	

	
	@Test
	public void testGlobFileListAndXPathSearchCommandResults() throws IOException {
		File targetDir = new File("target/glob/project2/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/"), targetDir);
		String output = "snippets.xml";
		String args = " --project " + targetDir+" --filter file(**/results.xml)xpath(//result) -o "+output;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		Assert.assertEquals("trees", 3,  argProcessor.getCTreeList().size());
//		for (CTree cTree : argProcessor.getCTreeList()) {
//			SnippetsTree snippetsTree = cTree.getSnippetsTree();
//			LOG.debug("SNIPz "+snippetsTree);
//		}
	}
	
	/** TEST METADATA FILES.
	 * 
	 * @throws IOException
	 */
	@Test 
	public void testMetadataFiles() {
		CProject project = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/"));
		List<AbstractMetadata.Type> types = project.getExistingMetadataTypes();
		Assert.assertEquals("[EPMC]", types.toString());
		LOG.debug(types);
		
	}

	/** MERGE PROJECTS.
	 * 
	 * @throws IOException
	 */
	@Test 
	public void testMergeProjects() throws IOException {
		File project1Dir = new File(CMineFixtures.TEST_PROJECTS_DIR, "project1/");
		File project2Dir = new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "project1/");
		File target2Dir = new File(CMineFixtures.GETPAPERS_TARGET, "project2/");
		CMineTestFixtures.cleanAndCopyDir(project1Dir, target1Dir);
		CMineTestFixtures.cleanAndCopyDir(project2Dir, target2Dir);
		CProject project1 = new CProject(target1Dir);
		CProject project2 = new CProject(target2Dir);
		Assert.assertEquals("ctree1", 2, project1.getResetCTreeList().size());
		Assert.assertEquals("ctree2", 3, project2.getResetCTreeList().size());
		project1.mergeProject(project2);
		project2 = new CProject(target2Dir);
		Assert.assertEquals("ctree1", 5, project1.getResetCTreeList().size());
		Assert.assertEquals("ctree2", 3, project2.getResetCTreeList().size());
	}
	
	/** NORMALIZE DOI NAMES
	 * 
	 * @throws IOException
	 */
	@Ignore //fails on test ordering
	@Test 
	public void testNormalizeDOIBasedNames() {
		File project1Dir = new File(CMineFixtures.TEST_PROJECTS_DIR, "doiNames/");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "doiNames/");
		CMineTestFixtures.cleanAndCopyDir(project1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		CTreeList cTreeList = project1.getResetCTreeList();
		Assert.assertEquals("unnormalized ", "["
				+ "target/getpapers/doiNames/http_dx.doi.org_10.1088_1757-899x_106_1_012037,"
				+ " target/getpapers/doiNames/http_dx.doi.org_10.1088_1757-899x_106_1_012038,"
				+ " target/getpapers/doiNames/http_dx.doi.org_10.1103_p", 
				Utils.truncate(cTreeList.toString(), 0, 200));
		project1.normalizeDOIBasedDirectoryCTrees();
		Assert.assertEquals("normalized ", "["
				+ "target/getpapers/doiNames/10.1088_1757-899x_106_1_012037,"
				+ " target/getpapers/doiNames/10.1088_1757-899x_106_1_012038,"
				+ " target/getpapers/doiNames/10.1103_physrevb.93.075101,"
				+ " target/getpapers/doiNames/10.",
				Utils.truncate(cTreeList.toString(), 0, 200));
	}
	
	
	

	//==================================
	
	@Test
	public void testFilenameSet() throws IOException {
		Set<String> filenameSet = new HashSet<String>();
		filenameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.SEQUENCE_DNAPRIMER_SNIPPETS_XML).getOrCreateFilenameList());
		filenameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.GENE_HUMAN_SNIPPETS_XML).getOrCreateFilenameList());
		filenameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.SPECIES_BINOMIAL_SNIPPETS_XML).getOrCreateFilenameList());
		filenameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.SPECIES_GENUS_SNIPPETS_XML).getOrCreateFilenameList());
		Assert.assertEquals("all files", 152, filenameSet.size());
	}
	
	@Test
	public void testProjectNameSet() throws IOException {
		Set<String> projectNameSet = new HashSet<String>();
		projectNameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.SEQUENCE_DNAPRIMER_SNIPPETS_XML).getCTreeNameList());
		projectNameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.GENE_HUMAN_SNIPPETS_XML).getCTreeNameList());
		projectNameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.SPECIES_BINOMIAL_SNIPPETS_XML).getCTreeNameList());
		projectNameSet.addAll(CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, CProject.SPECIES_GENUS_SNIPPETS_XML).getCTreeNameList());
		Assert.assertEquals("all files", 90, projectNameSet.size());
	}



}
